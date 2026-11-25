// Kjører et scenario: node run.mjs scenarier/<fil>.mjs [argumenter]
// Et scenario eksporterer default async ({ som, fpsak, familie, args }) => {}.
//   const p = await som('mor', 'soknad', fam.mor)  -> innlogget side (Vite mode som standard), lukkes automatisk
//   const p = await fpsak(411)                       -> fpsak-frontend for saken
import path from 'node:path';
import fs from 'node:fs';
import { åpne, startBrowser, erPortÅpen, VITE_PORTER, loggInnSelvbetjening, loggInnFpsak } from './lib/browser.mjs';
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
// Kjøringer som henger, stopper seg selv (kill er ikke tillatt i cplt). TIDSGRENSE i sekunder, standard 150.
const tidsgrense = setTimeout(() => {
    console.error(`TIDSGRENSE: scenarioet brukte mer enn ${process.env.TIDSGRENSE ?? 150} s`);
    process.exit(2);
}, 1000 * Number(process.env.TIDSGRENSE ?? 150));
try {
    await scenario({ som, fpsak, familie, args });
} finally {
    for (const s of åpne_) await s.lukk().catch(() => {});
    if (deltBrowser) await deltBrowser.close().catch(() => {});
    clearTimeout(tidsgrense);
}
