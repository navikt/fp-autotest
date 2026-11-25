// Felles oppstart og innlogging mot lokal verdikjede (fp-autotest/lokal-utvikling).
import { chromium } from 'playwright';
import fs from 'node:fs';
import net from 'node:net';
import path from 'node:path';

const ROOT = path.resolve(import.meta.dirname, '..');
export const STATE_DIR = path.join(ROOT, '.state');
export const SHOT_DIR = path.join(STATE_DIR, 'skjermbilder');
export const VITE_PORTER = { soknad: 9114, oversikt: 9110 };

function prøvPort(host, port, timeoutMs) {
    return new Promise((resolve) => {
        const s = net.createConnection({ host, port });
        const ferdig = (ok) => {
            s.destroy();
            resolve(ok);
        };
        s.setTimeout(timeoutMs, () => ferdig(false));
        s.once('connect', () => ferdig(true));
        s.once('error', () => ferdig(false));
    });
}

const portCache = new Map();
export async function erPortÅpen(port, timeoutMs = 150) {
    if (portCache.has(port)) return portCache.get(port);
    const [v4, v6] = await Promise.all([prøvPort('127.0.0.1', port, timeoutMs), prøvPort('::1', port, timeoutMs)]);
    const ok = v4 || v6;
    portCache.set(port, ok);
    return ok;
}

export function startBrowser() {
    return chromium.launch({ executablePath: process.env.CHROMIUM || undefined });
}

export async function åpne({ state, browser: delt } = {}) {
    fs.mkdirSync(STATE_DIR, { recursive: true });
    const browser = delt ?? (await startBrowser());
    const statePath = state ? path.join(STATE_DIR, `${state}.json`) : undefined;
    const ctx = await browser.newContext({
        viewport: { width: 1300, height: 1000 },
        storageState: statePath && fs.existsSync(statePath) ? statePath : undefined,
    });
    const page = await ctx.newPage();
    page.setDefaultTimeout(10000); // ingen handling skal vente mer enn 10 s lokalt
    page.setDefaultNavigationTimeout(20000);
    // Ufangede feil i siden (f.eks. kast i onSubmit) vises ellers ikke, og ser ut som om knappen ikke virker.
    page.on('pageerror', (e) => console.log('SIDEFEIL', e.message.slice(0, 300)));
    if (process.env.KONSOLL) page.on('console', (m) => m.type() === 'error' && console.log('KONSOLL', m.text().slice(0, 300)));
    page.on('response', (r) => {
        if (r.status() >= 400 && !r.url().includes('login.nav.no')) console.log('HTTP', r.status(), r.url().slice(0, 150));
        // NAVIGASJON=1 viser dokumentforespørsler (metode, status, adresse uten query), f.eks. for å følge innloggingen.
        if (process.env.NAVIGASJON && r.request().resourceType() === 'document') {
            const loc = r.headers()['location']?.split('?')[0];
            console.log('NAV', r.request().method(), r.status(), r.url().split('?')[0], loc ? `-> ${loc}` : '');
        }
    });
    const lukk = async () => {
        if (statePath) {
            const tmp = `${statePath}.${process.pid}.tmp`;
            try {
                await ctx.storageState({ path: tmp });
                // Rename gjør skrivingen atomisk når flere kjøringer deler fpsak.json.
                fs.renameSync(tmp, statePath);
            } finally {
                fs.rmSync(tmp, { force: true });
            }
        }
        await ctx.close();
        if (!delt) await browser.close();
    };
    return { browser, ctx, page, lukk };
}

// Selvbetjening via wonderwall (ID-porten i VTP). vite: port til lokal vite-server skal brukes (cookie via /vite-on).
export const APPER = {
    soknad: 'http://localhost:9101/foreldrepenger/soknad',
    oversikt: 'http://localhost:9100/foreldrepenger/oversikt',
    svp: 'http://localhost:9102/svangerskapspenger/soknad',
    es: 'http://localhost:9103/engangsstonad',
};

const APP_KLAR = '#app :is(h1,h2,button,input)';

// VTP lagrer nonce fra ID-porten i én felles plass, så samtidige innlogginger overskriver hverandre
// (se «Kjente fallgruver» i README.md). Låsen slipper én innlogging gjennom om gangen på tvers av prosesser,
// fra skjemaet åpnes til Wonderwall har vekslet koden i callbacken.
const INNLOGGING_LÅS = path.join(STATE_DIR, '.innlogging-lock');

function låsErForeldet() {
    try {
        const pid = Number(fs.readFileSync(path.join(INNLOGGING_LÅS, 'pid'), 'utf8'));
        try {
            process.kill(pid, 0);
        } catch (e) {
            if (e.code === 'ESRCH') return true; // eieren lever ikke lenger (f.eks. stoppet av TIDSGRENSE)
        }
    } catch {
        // pid-fila mangler eller skrives akkurat nå, se på alderen
    }
    try {
        return Date.now() - fs.statSync(INNLOGGING_LÅS).mtimeMs > 60000;
    } catch {
        return false;
    }
}

async function taInnloggingslås() {
    fs.mkdirSync(STATE_DIR, { recursive: true });
    const start = Date.now();
    for (;;) {
        try {
            fs.mkdirSync(INNLOGGING_LÅS);
            fs.writeFileSync(path.join(INNLOGGING_LÅS, 'pid'), String(process.pid));
            break;
        } catch (e) {
            if (e.code !== 'EEXIST') throw e;
            if (låsErForeldet()) fs.rmSync(INNLOGGING_LÅS, { recursive: true, force: true });
            else await new Promise((r) => setTimeout(r, 100));
        }
    }
    const ventet = (Date.now() - start) / 1000;
    if (ventet >= 1) console.log(`INNLOGGINGSLÅS ventet ${ventet.toFixed(1)} s`);
    let sluppet = false;
    return () => {
        if (sluppet) return;
        sluppet = true;
        fs.rmSync(INNLOGGING_LÅS, { recursive: true, force: true });
    };
}

export async function loggInnSelvbetjening(page, app, fnr, { vite = true } = {}) {
    const url = APPER[app] ?? app;
    const slippLås = await taInnloggingslås();
    try {
        await page.goto(url, { waitUntil: 'domcontentloaded' });
        await page.locator(`input[name=code], ${APP_KLAR}`).first().waitFor({ timeout: 20000 });
        if (await page.locator('input[name=code]').count()) {
            await page.fill('input[name=code]', fnr);
            const callback = page.waitForResponse((r) => r.url().includes('/oauth2/callback'), { timeout: 20000 });
            await page.click('input[type=submit]');
            await callback; // Wonderwall har vekslet koden hos VTP, nonce er brukt
            slippLås();
            await page.locator(APP_KLAR).first().waitFor({ timeout: 20000 }).catch(async (e) => {
                // Viser hvor nettleseren havnet etter innloggingen (adresse uten query og sidetekst).
                const tekst = (await page.locator('body').innerText().catch(() => ''));
                console.log('INNLOGGING STOPPET', page.url().split('?')[0], '|', tekst.replace(/\s+/g, ' ').slice(0, 400));
                throw e;
            });
        }
    } finally {
        slippLås();
    }
    if (vite) {
        const cookies = await page.context().cookies(url);
        if (!cookies.some((c) => /vite/i.test(c.name))) {
            await page.goto(`${url}/vite-on`, { waitUntil: 'domcontentloaded' });
            await page.goto(url, { waitUntil: 'domcontentloaded' });
            await page.locator(APP_KLAR).first().waitFor({ timeout: 20000 });
        }
    }
}

// fpsak-frontend via Entra ID i VTP.
export async function loggInnFpsak(page, saksnummer, bruker = 'Sara Saksbehandler') {
    await page.goto(`http://localhost:9000/fagsak/${saksnummer}/`, { waitUntil: 'domcontentloaded' });
    const brukerLoc = page.getByText(bruker).first();
    const klarLoc = page.getByText(/Behandlingsstatus|Velg behandling/).first();
    await Promise.race([brukerLoc.waitFor({ timeout: 20000 }), klarLoc.waitFor({ timeout: 20000 })]);
    if (await page.getByText(bruker).count()) {
        // VTP-siden kan ha et lag over seg mens den laster. Da prøver vi igjen med force.
        await page.getByText(bruker).click({ timeout: 5000 }).catch(() => page.getByText(bruker).click({ force: true }));
        await page.waitForURL(/fagsak/);
    }
    await klarLoc.waitFor({ timeout: 20000 });
    await page.waitForLoadState('networkidle');
}

export async function skjermbilde(page, navn) {
    fs.mkdirSync(SHOT_DIR, { recursive: true });
    const fil = path.join(SHOT_DIR, `${navn}.png`);
    await page.screenshot({ path: fil, fullPage: true });
    console.log('SKJERMBILDE', fil);
    return fil;
}

export const tekstlinjer = async (loc) =>
    (await loc.innerText())
        .split('\n')
        .map((s) => s.trim())
        .filter((s) => s && !/^([0-9]{1,2}|ma|ti|on|to|fr|lø|sø)$/.test(s));

// spec 'navn=verdi,label:Ledetekst=verdi,velg:Ledetekst=Valgtekst,sjekk:Ledetekst,klikk:Knappetekst,...': krysser av radio/checkbox med verdien, ellers fylles feltet (input, textarea, select).
// klikk: klikker knappen med teksten. Gjøres i rekkefølge, slik at betingede felt rekker å dukke opp.
export async function fyllFelt(p, spec) {
    for (const par of (spec ?? '').split(',').filter(Boolean)) {
        if (par.startsWith('sjekk:')) {
            await p.getByLabel(par.slice(6)).first().check();
            await p.waitForTimeout(600);
            continue;
        }
        if (par.startsWith('klikk:')) {
            await p.getByRole('button', { name: par.slice(6), exact: true }).first().click();
            await p.waitForTimeout(800);
            continue;
        }
        const i = par.indexOf('=');
        const [n, v] = [par.slice(0, i), par.slice(i + 1)];
        if (n.startsWith('velg:')) {
            await p.getByRole('combobox', { name: n.slice(5) }).selectOption({ label: v });
            await p.waitForTimeout(500);
            continue;
        }
        if (n.startsWith('label:')) {
            await p.getByLabel(n.slice(6)).first().fill(v);
            await p.keyboard.press('Tab');
            await p.waitForTimeout(500);
            continue;
        }
        const valg = p.locator(`input[name="${n}"][value="${v}"]`);
        if (await valg.count()) await valg.first().check();
        else {
            const felt = p.locator(`[name="${n}"]`).first();
            if ((await felt.evaluate((e) => e.tagName)) === 'SELECT') await felt.selectOption(v);
            else await felt.fill(v);
            await p.keyboard.press('Tab');
        }
        await p.waitForTimeout(500);
    }
}
