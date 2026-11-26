// Kjører et scenario: node run.mjs scenarier/<fil>.mjs [argumenter]
// Et scenario eksporterer default async ({ som, fpsak, familie, args }) => {}.
//   const p = await som('mor', 'soknad', fam.mor)  -> innlogget side (Vite mode som standard), lukkes automatisk
//   const p = await fpsak(411)                       -> fpsak-frontend for saken
import path from 'node:path';
import fs from 'node:fs';
import { åpne, startBrowser, erPortÅpen, erSynlig, VITE_PORTER, loggInnSelvbetjening, loggInnFpsak } from './lib/browser.mjs';
import { familie } from './lib/familie.mjs';

const [fil, ...args] = process.argv.slice(2);
if (!fil) {
    console.error('Bruk: node run.mjs scenarier/<fil>.mjs [argumenter]');
    process.exit(1);
}
const åpne_ = [];
const loggetModus = new Set();
let deltBrowser;
const hentBrowser = async () => (deltBrowser ??= await startBrowser());

// VITE=0 tvinger Docker-imaget, VITE=1 tvinger Vite. Uten env-variabel sjekkes lokal Vite-port automatisk.
// Med Docker får state-filen -docker, så en gammel Vite-cookie ikke blir med.
const som = async (state, app, fnr, opt = {}) => {
    let vite;
    if (process.env.VITE === '0') vite = false;
    else if (process.env.VITE === '1') vite = true;
    else if (opt.vite !== undefined) vite = opt.vite;
    else if (VITE_PORTER[app]) {
        const port = VITE_PORTER[app];
        vite = await erPortÅpen(port);
        if (!vite && !loggetModus.has(app)) {
            loggetModus.add(app);
            console.log(`MODUS: docker (vite-port ${port} svarer ikke)`);
        }
    } else vite = true;
    const s = await åpne({ state: vite ? state : `${state}-docker`, browser: await hentBrowser() });
    åpne_.push(s);
    await loggInnSelvbetjening(s.page, app, fnr, { ...opt, vite });
    return s.page;
};
// fpsak(sak, { bruker: 'Birger Beslutter' }) logger inn som en annen VTP-bruker med egen state.
const fpsak = async (saksnummer, opt = {}) => {
    const s = await åpne({
        state: opt.bruker ? `fpsak-${opt.bruker.split(' ')[0].toLowerCase()}` : 'fpsak',
        browser: await hentBrowser(),
    });
    åpne_.push(s);
    await loggInnFpsak(s.page, saksnummer, opt.bruker);
    return s.page;
};
const scenarioFil = path.resolve(fil);
const scenarioPath = fs.existsSync(scenarioFil) ? scenarioFil : path.resolve(import.meta.dirname, fil);
const scenario = (await import(scenarioPath)).default;
// Kjøringer som henger, stopper seg selv (kill er ikke tillatt i cplt). TIDSGRENSE i sekunder, standard 150 (300 med synlig nettleser).
const tidsgrenseSek = Number(process.env.TIDSGRENSE ?? (erSynlig() ? 300 : 150));
const tidsgrense = setTimeout(() => {
    console.error(`TIDSGRENSE: scenarioet brukte mer enn ${tidsgrenseSek} s`);
    process.exit(2);
}, 1000 * tidsgrenseSek);
let feil;
let skalHolde;
try {
    await scenario({ som, fpsak, familie, args });
} catch (e) {
    feil = e;
} finally {
    clearTimeout(tidsgrense);
    for (const s of åpne_) await s.lagreState().catch(() => {});
    const ventPåLukking = () => {
        const åpneSider = åpne_.map((s) => s.page).filter((p) => !p.isClosed());
        if (!deltBrowser.isConnected() || !åpneSider.length) return Promise.resolve();
        return Promise.race([
            new Promise((resolve) => deltBrowser.once('disconnected', resolve)),
            Promise.all(åpneSider.map((p) => new Promise((resolve) => p.once('close', resolve)))),
        ]);
    };
    skalHolde = erSynlig() && deltBrowser && process.env.HOLD !== '0' && (process.env.HOLD === '1' || Boolean(feil));
    if (skalHolde) {
        if (feil) console.error(feil);
        console.log('NETTLESER ÅPEN: lukk vinduet når du er ferdig.');
        await ventPåLukking();
    } else if (erSynlig() && deltBrowser && process.env.HOLD !== '0') {
        const pauseSek = Number(process.env.SLUTTPAUSE ?? 10);
        if (pauseSek > 0) {
            console.log(`NETTLESER ÅPEN i ${pauseSek} s (eller til du lukker vinduet).`);
            let pauseTimer;
            await Promise.race([
                ventPåLukking(),
                new Promise((resolve) => { pauseTimer = setTimeout(resolve, pauseSek * 1000); }),
            ]);
            clearTimeout(pauseTimer);
        }
    }
    for (const s of åpne_) await s.lukk().catch(() => {});
    if (deltBrowser) await deltBrowser.close().catch(() => {});
}
if (feil) {
    if (skalHolde) process.exitCode = 1;
    else throw feil;
}
