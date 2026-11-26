// Foreldrepengesøknaden: fyll ut med standardsvar, rediger planen og send. Velgere er hentet fra appen per nov. 2026.
import { tekstlinjer } from './browser.mjs';

const app = (p) => p.locator('#app');
const steg = (p) => p.url().split('/').pop();

function datoTilbake({ år = 0, måneder = 0 } = {}) {
    const d = new Date();
    d.setFullYear(d.getFullYear() - år, d.getMonth() - måneder, 1);
    return `${String(d.getDate()).padStart(2, '0')}.${String(d.getMonth() + 1).padStart(2, '0')}.${d.getFullYear()}`;
}

async function ventPåStegEndring(p, forrige, gammelForm) {
    await Promise.race([
        p.waitForFunction((før) => location.pathname.split('/').pop() !== før, forrige, { timeout: 5000 }),
        app(p).locator('[class*=error-message]').first().waitFor({ timeout: 5000 }),
    ]).catch(() => {});
    if (steg(p) !== forrige) {
        if (gammelForm) await p.waitForFunction((el) => !el.isConnected, gammelForm, { timeout: 5000 }).catch(() => {});
        if (steg(p) === 'kvitttering') return;
        await app(p).locator('form :is(input, button[type=submit]), button:has-text("Neste steg"), button:has-text("Send søknaden")').first().waitFor({ state: 'visible', timeout: 10000 }).catch(() => {});
        await app(p).locator('.navds-loader, .aksel-loader').first().waitFor({ state: 'hidden', timeout: 5000 }).catch(() => {});
    }
}

async function velg(p, navn, verdi, { overstyr = false } = {}) {
    const l = p.locator(`input[name="${navn}"][value="${verdi}"]`);
    if (!(await l.count())) return;
    if (!overstyr && (await p.locator(`input[name="${navn}"]:checked`).count())) return;
    await l.first().check();
}

// Forsiden har bekreftelsen harForståttRettigheterOgPlikter. Oppsummeringen har en avkrysning med samme navn,
// men den har også «Slett søknaden», så den knappen skiller et utkast fra forsiden.
async function erForside(p) {
    if (!(await p.locator('input[name=harForståttRettigheterOgPlikter]').count())) return false;
    return !(await p.getByRole('button', { name: 'Slett søknaden' }).count());
}

export async function slettUtkast(p) {
    await p
        .locator('input[name=harForståttRettigheterOgPlikter], button:has-text("Start på nytt"), button:has-text("Slett søknaden")')
        .first()
        .waitFor({ timeout: 20000 })
        .catch(() => {});
    if (await erForside(p)) return false;
    const påNytt = p.getByRole('button', { name: 'Start på nytt' });
    if (await påNytt.count()) {
        await påNytt.click();
        await p.locator('input[name=harForståttRettigheterOgPlikter]').waitFor({ timeout: 10000 }).catch(() => {});
        console.log('Opplysninger endret: startet på nytt');
        return true;
    }
    await p.getByRole('button', { name: 'Slett søknaden' }).click();
    const dialog = p.getByRole('dialog');
    await dialog.waitFor({ timeout: 5000 }).catch(() => {});
    const bekreft = dialog.getByRole('button', { name: /Slett/ });
    await bekreft.first().waitFor({ timeout: 5000 }).catch(() => {});
    if (await bekreft.count()) await bekreft.first().click();
    await dialog.waitFor({ state: 'hidden', timeout: 10000 }).catch(() => {});
    await p.reload({ waitUntil: 'domcontentloaded' });
    await p.locator('input[name=harForståttRettigheterOgPlikter]').waitFor({ timeout: 15000 }).catch(() => {});
    console.log('Utkast slettet');
    return true;
}

// Forsiden: velg barn (index eller regex på label, eller "nytt") og start/endre.
export async function start(p, { barn = 0 } = {}) {
    if (!(await erForside(p))) return; // utkast fortsetter
    const radios = p.locator('input[name=valgteBarn]');
    if (await radios.count()) {
        if (barn === 'nytt') await p.locator('input[name=valgteBarn][value=søknad_gjelder_nytt_barn]').check();
        else if (barn instanceof RegExp) await p.getByLabel(barn).check();
        else await radios.nth(barn).check();
    }
    await p.getByLabel('Jeg bekrefter at jeg har lest og forstått').check();
    const forrige = steg(p);
    const gammelForm = await app(p).locator('form').first().elementHandle().catch(() => null);
    await app(p).getByRole('button').filter({ hasText: /Start søknaden|Endre søknad/ }).first().click();
    await ventPåStegEndring(p, forrige, gammelForm);
}

/**
 * Går gjennom stegene med standardsvar til `stoppVed` (steg-navn i URL) eller oppsummering/kvittering.
 * opt: rolle ('mor'|'far'), termin ('dd.mm.åååå' når barnet ikke er født), nyTermin (overstyrer forhåndsutfylt),
 *      fødselsdato ('dd.mm.åååå' for født barn som ikke er registrert, sammen med fødselTermin),
 *      annenPart ({ fnr, fornavn, etternavn } når barnet ikke er registrert), dekningsgrad ('100'|'80'),
 *      fordeling ('ALT'|'VIL_VELGE'|'HOPP_OVER_FORDELING'), oppstart (verdi, ellers første valg),
 *      situasjon ('fødsel'|'adopsjon'), stebarn, omsorgsovertakelse, adoptertFødt, ankomstdato ('dd.mm.åååå', adopsjon; nytt barn på forsiden),
 *      annenForelder ({ aleneomsorg, harRett (standard true), eøs: { rett }, ufør, utenlandsk: { land } }), der land er landnavnet, f.eks. 'Sverige',
 *      varigEndring (true eller { dato, inntekt, beskrivelse } for næring), vedPlan (async p => {...} for å redigere planen), justerVedFødsel (far ved termin, standard true), send (true sender), logg (regex for linjer som skal skrives ut).
 */
export async function fyllUt(p, opt = {}) {
    const logg = opt.logg ?? /hentet fra|allerede brukt|Fra .* søknad|siste dag|uker igjen|lagt inn i planen|ligger ikke|for mye/;
    for (let i = 0; i < 14; i++) {
        const u = steg(p);
        const linjer = await tekstlinjer(app(p));
        console.log(`--- ${u} | ${linjer.filter((s) => logg.test(s)).join(' || ')}`);
        if (u === opt.stoppVed) return u;
        if (u === 'kvitttering') return u;
        if (u === 'oppsummering') {
            if (!opt.send) return u;
            await sendInn(p);
            continue;
        }
        await velg(p, 'situasjon', opt.situasjon ?? 'fødsel', { overstyr: !!opt.situasjon });
        await velg(p, 'rolle', opt.rolle === 'far' ? 'far' : 'mor');
        if (u === 'barnet' && opt.situasjon === 'adopsjon') {
            await velg(p, 'adopsjonAvEktefellesBarn', opt.stebarn ? 'true' : 'false');
            const omsorg = p.getByLabel(/Når overtar du omsorgen|Når overtok du omsorgen/);
            await omsorg.first().waitFor({ timeout: 2000 }).catch(() => {});
            if ((await omsorg.count()) && !(await omsorg.first().inputValue())) {
                await omsorg.first().fill(opt.omsorgsovertakelse);
                await p.keyboard.press('Tab');
            }
            await velg(p, 'antallBarn', '1');
            const født = p.getByLabel('Når ble barnet født?');
            await født.waitFor({ timeout: 2000 }).catch(() => {});
            if ((await født.count()) && !(await født.first().inputValue())) {
                await født.first().fill(opt.adoptertFødt ?? datoTilbake({ år: 1 }));
                await p.keyboard.press('Tab');
            }
            await velg(p, 'adoptertIUtlandet', opt.ankomstdato ? 'true' : 'false');
            const ankomst = p.getByLabel(/ankom|ankomst/i);
            if (opt.ankomstdato && (await ankomst.count()) && !(await ankomst.first().inputValue())) {
                await ankomst.first().fill(opt.ankomstdato);
                await p.keyboard.press('Tab');
            }
        } else if (u === 'barnet') {
            await velg(p, 'erBarnetFødt', opt.termin ? 'false' : 'true');
            await velg(p, 'antallBarn', '1');
            if (opt.fødselsdato || opt.termin || opt.nyTermin || opt.fødselTermin)
                await p.getByLabel(/termindatoen|Når ble barnet født/).first().waitFor({ timeout: 1500 }).catch(() => {});
            if (opt.fødselsdato) {
                // Barnet er født, men ikke registrert på søkeren
                const fd = p.getByLabel('Når ble barnet født?');
                if (await fd.count()) {
                    await fd.first().fill(opt.fødselsdato);
                    await p.keyboard.press('Tab');
                }
            }
            const ti = p.getByLabel(/termindatoen/);
            if (await ti.count()) {
                const nå = await ti.first().inputValue();
                console.log('    termin forhåndsutfylt:', JSON.stringify(nå));
                const ny = opt.nyTermin ?? (nå ? undefined : opt.termin ?? opt.fødselTermin);
                if (ny) {
                    await ti.first().fill(ny);
                    await p.keyboard.press('Tab');
                }
            }
        }
        await velg(p, 'harBoddUtenforNorgeSiste12Mnd', 'false');
        await velg(p, 'skalBoUtenforNorgeNeste12Mnd', 'false');
        if (u === 'frilans') await velg(p, 'jobberFremdelesSomFrilans', 'true');
        if (u === 'selvstendig-naringsdrivende') {
            const start = p.getByLabel('Når startet du virksomheten?');
            if ((await start.count()) && !(await start.inputValue())) {
                await start.fill(opt.næringStart ?? datoTilbake({ år: 5 }));
                await p.keyboard.press('Tab');
            }
            await velg(p, 'pågående', 'true');
            const inntekt = p.locator('input[name=næringsinntekt]');
            if ((await inntekt.count()) && !(await inntekt.inputValue())) await inntekt.fill('500000');
            await velg(p, 'harBlittYrkesaktivILøpetAvDeTreSisteFerdigliknedeÅrene', 'false');
            await p.locator('input[name=hattVarigEndringAvNæringsinntektSiste4Kalenderår]').first().waitFor({ timeout: 2000 }).catch(() => {});
            await velg(p, 'hattVarigEndringAvNæringsinntektSiste4Kalenderår', opt.varigEndring ? 'true' : 'false', { overstyr: !!opt.varigEndring });
            if (opt.varigEndring) {
                await p.locator('input[name=hattVarigEndringAvNæringsinntektSiste4Kalenderår][value=true]').check();
                const ve = typeof opt.varigEndring === 'object' ? opt.varigEndring : {};
                // Datovelgeren har ikke name-attributt, så feltene finnes via ledeteksten.
                const dato = p.getByLabel('Hva var datoen for endringen?');
                await dato.waitFor({ timeout: 2000 }).catch(() => {});
                await dato.fill(ve.dato ?? datoTilbake({ måneder: 6 }));
                await p.keyboard.press('Tab');
                await p.getByLabel('Hva var næringsinntekten din etter endringen?').fill(String(ve.inntekt ?? 600000));
                await p.getByLabel('Hva har endret seg i virksomheten eller arbeidssituasjonen din?').fill(ve.beskrivelse ?? 'Økt omsetning');
            }
        }
        if (u === 'den-andre-forelderen') {
            const af = opt.annenForelder ?? {};
            if (af.utenlandsk) {
                // Krysses av før fnr fylles inn, ellers er landvelgeren ikke aktiv.
                const boks = p.getByLabel('Fødselsnummeret er ikke fra Norge');
                if ((await boks.count()) && !(await boks.isChecked())) await boks.check();
                await p.getByRole('combobox', { name: 'Hvor bor den andre forelderen?' }).waitFor({ timeout: 2000 }).catch(() => {});
            }
            const fnr = p.locator('input[name=fnr]');
            if (opt.annenPart && (await fnr.count())) {
                for (const [felt, verdi] of [['fornavn', opt.annenPart.fornavn ?? 'Test'], ['etternavn', opt.annenPart.etternavn ?? 'Forelder']]) {
                    const l = p.locator(`input[name=${felt}]`);
                    if ((await l.count()) && !(await l.inputValue())) await l.fill(verdi);
                }
                if (!(await fnr.inputValue())) await fnr.fill(opt.annenPart.fnr);
                await p.keyboard.press('Tab');
                await p.locator('input[name=erAleneOmOmsorg]').first().waitFor({ timeout: 5000 }).catch(() => {});
            }
            if (af.utenlandsk) {
                await p.getByRole('combobox', { name: 'Hvor bor den andre forelderen?' }).selectOption({ label: af.utenlandsk.land ?? 'Sverige' });
            }
            await velg(p, 'erAleneOmOmsorg', af.aleneomsorg ? 'true' : 'false', { overstyr: true });
            if (!af.aleneomsorg) {
                await p.locator('input[name=harRettPåForeldrepengerINorge]').first().waitFor({ timeout: 2000 }).catch(() => {});
                const harRett = af.harRett ?? true;
                await velg(p, 'harRettPåForeldrepengerINorge', String(harRett), { overstyr: true });
                if (!harRett) {
                    await p.locator('input[name=harOppholdtSegIEØS]').first().waitFor({ timeout: 2000 }).catch(() => {});
                    await velg(p, 'harOppholdtSegIEØS', String(!!af.eøs), { overstyr: true });
                    await p.locator(`input[name=${af.eøs ? 'harRettPåForeldrepengerIEØS' : 'erMorUfør'}]`).first().waitFor({ timeout: 2000 }).catch(() => {});
                    if (af.eøs) await velg(p, 'harRettPåForeldrepengerIEØS', String(af.eøs.rett ?? true), { overstyr: true });
                    else await velg(p, 'erMorUfør', String(!!af.ufør), { overstyr: true });
                }
                await velg(p, 'erInformertOmSøknaden', 'true');
            }
        }
        if (u === 'periode') {
            const forhånd = await p.locator('input[name=dekningsgrad]:checked').evaluateAll((e) => e.map((x) => x.value));
            console.log('    dekningsgrad forhåndsvalgt:', JSON.stringify(forhånd));
            await velg(p, 'dekningsgrad', opt.dekningsgrad ?? '100', { overstyr: !!opt.dekningsgrad });
        }
        if (u === 'fordeling') {
            await velg(p, 'fordelingValg', opt.fordeling ?? 'ALT');
            if (opt.oppstart) await velg(p, 'oppstartAvForeldrepengerValg', opt.oppstart);
            else if (!(await p.locator('input[name=oppstartAvForeldrepengerValg]:checked').count())) {
                const første = p.locator('input[name=oppstartAvForeldrepengerValg]').first();
                if (await første.count()) await første.check();
            }
        }
        if (u === 'plan' && opt.vedPlan) await opt.vedPlan(p);
        // Spørsmålet vises for far/medmor ved termin når planen kan justeres rundt fødsel (kan endres av vedPlan).
        if (u === 'plan') await velg(p, 'ønskerJustertUttakVedFødsel', opt.justerVedFødsel === false ? 'false' : 'true');
        await lastOppVedlegg(p);
        const gammelForm = await app(p).locator('form').first().elementHandle().catch(() => null);
        await p.getByRole('button', { name: 'Neste steg' }).click();
        await ventPåStegEndring(p, u, gammelForm);
        if (steg(p) === u) {
            console.log(`STOPPET på ${u}: ${(await tekstlinjer(app(p))).filter((s) => /må|feil/i.test(s)).join(' | ')}`);
            console.log('FELT', JSON.stringify(await app(p).locator('input').evaluateAll((e) => e.map((x) => `${x.name}=${x.value}${x.checked ? '*' : ''}`))));
            return u;
        }
    }
}

// Laster opp lib/vedlegg.pdf i hvert filfelt på steget som ikke har fått fil ennå. Filfelt finnes på dokumentasjonsstegene.
const VEDLEGG = new URL('./vedlegg.pdf', import.meta.url).pathname;
async function lastOppVedlegg(p) {
    const filer = app(p).locator('input[type=file]');
    for (let i = 0; i < (await filer.count()); i++) {
        const f = filer.nth(i);
        if (await f.evaluate((e) => e.dataset.lastetOpp === '1')) continue;
        await f.setInputFiles(VEDLEGG);
        await f.evaluate((e) => (e.dataset.lastetOpp = '1')).catch(() => {});
        await app(p).getByText('vedlegg.pdf').first().waitFor({ timeout: 4000 }).catch(() => {});
    }
}

export async function oppsummeringPlan(p) {
    const t = await app(p).innerText();
    return t.slice(t.indexOf('Din plan'), t.indexOf('Jeg har gjort meg kjent')).replace(/\n\s*\n/g, '\n').replace(/\n/g, ' / ');
}

export async function sendInn(p) {
    console.log('OPPSUMMERING', await oppsummeringPlan(p));
    const cb = app(p).locator('input[type=checkbox]');
    for (let j = 0; j < (await cb.count()); j++) await cb.nth(j).check();
    await p.getByRole('button', { name: 'Send søknaden' }).click();
    await p.waitForURL(/kvitt/, { timeout: 10000 }).catch(() => {});
    console.log('SENDT', steg(p));
}

// ---- Planen (steg «plan») ----

function brukKalender(visning) {
    return (visning ?? process.env.VISNING ?? 'kalender') !== 'liste';
}

function parseDato(s) {
    const m1 = /^(\d{1,2})\.(\d{1,2})\.(\d{4})$/.exec(s.trim());
    if (m1) return new Date(Date.UTC(+m1[3], +m1[2] - 1, +m1[1]));
    const m2 = /^(\d{4})-(\d{1,2})-(\d{1,2})$/.exec(s.trim());
    if (m2) return new Date(Date.UTC(+m2[1], +m2[2] - 1, +m2[3]));
    throw new Error(`Ukjent datoformat: ${s} (forventet dd.mm.åååå eller åååå-mm-dd)`);
}

function tilUkedag(d, retning) {
    const kopi = new Date(d.getTime());
    const dag = kopi.getUTCDay(); // 0 = søn, 6 = lør
    if (dag === 6) kopi.setUTCDate(kopi.getUTCDate() + (retning > 0 ? 2 : -1));
    else if (dag === 0) kopi.setUTCDate(kopi.getUTCDate() + (retning > 0 ? 1 : -2));
    return kopi;
}

export async function visKalender(p) {
    const fane = p.getByRole('tab', { name: 'Kalender' });
    if ((await fane.count()) && (await fane.getAttribute('aria-selected')) !== 'true') {
        await fane.click();
    }
    await p.locator('[data-month-key]').first().waitFor({ timeout: 3000 }).catch(() => {});
}

export async function visListe(p) {
    // En popover i kalenderen (f.eks. varsel om dager som ikke ligger i planen) kan ligge over «Liste» når musa står
    // over en dag. Flytt musa bort og lukk popoveren først.
    await p.mouse.move(0, 0);
    await p.keyboard.press('Escape');
    const fane = p.getByRole('tab', { name: 'Liste' });
    if (await fane.count()) await fane.click();
    else await p.getByText('Liste', { exact: true }).click();
    await p.getByRole('button', { name: 'Legg til periode' }).waitFor({ timeout: 3000 }).catch(() => {});
}

async function finnMånedIKalender(p, år, måned) {
    const mål = p.locator(`[data-testid="year:${år};month:${måned}"]`);
    if (await mål.count()) return mål;
    const startRedigering = p.getByRole('button', { name: 'Start redigering' });
    if (await startRedigering.isVisible().catch(() => false)) {
        await startRedigering.click();
    }
    for (let i = 0; i < 12 && !(await mål.count()); i++) {
        const nøkler = await p.locator('[data-month-key]').evaluateAll((els) => els.map((e) => e.getAttribute('data-month-key')));
        if (!nøkler.length) break;
        const [førsteÅr, førsteMnd] = nøkler[0].split('-').map(Number);
        const erFør = år < førsteÅr || (år === førsteÅr && måned < førsteMnd);
        const knapper = p.getByRole('button', { name: 'Vis flere måneder' });
        const antall = await knapper.count();
        if (!antall) break;
        const knapp = erFør ? knapper.first() : knapper.last();
        await knapp.click();
        await p.waitForTimeout(100);
    }
    if (!(await mål.count())) throw new Error(`Fant ikke måned ${år}-${måned + 1} i kalenderen`);
    return mål;
}

export async function velgDatoerIKalender(p, fom, tom) {
    await visKalender(p);
    // Nullstill eventuelt tidligere valg i kalenderen.
    const valgtChip = p.getByRole('button', { name: /valgt/i });
    if (await valgtChip.first().isVisible().catch(() => false)) {
        await valgtChip.first().click().catch(() => {});
    }
    // Sørg for at «Periode (fra - til)» er valgt dersom redigeringsmodus allerede er aktiv.
    const periodeRadio = p.getByLabel('Periode (fra - til)');
    if (await periodeRadio.isVisible().catch(() => false)) {
        if (!(await periodeRadio.isChecked().catch(() => true))) await periodeRadio.check();
    }
    const d1 = tilUkedag(parseDato(fom), 1);
    const d2 = tilUkedag(parseDato(tom), -1);
    const m1 = await finnMånedIKalender(p, d1.getUTCFullYear(), d1.getUTCMonth());
    await m1.locator(`[data-testid^="day:${d1.getUTCDate()};"]`).click();
    if (d1.getTime() !== d2.getTime()) {
        const m2 = await finnMånedIKalender(p, d2.getUTCFullYear(), d2.getUTCMonth());
        await m2.locator(`[data-testid^="day:${d2.getUTCDate()};"]`).click();
    }
    await p.getByRole('heading', { name: 'Hva vil du endre til?' }).waitFor({ timeout: 3000 });
}

// Velger én eller flere enkeltdager i kalenderen (f.eks. ['12.07.2027', '14.07.2027', '16.07.2027']).
export async function velgEnkeltdagerIKalender(p, datoer) {
    await visKalender(p);
    const startRedigering = p.getByRole('button', { name: 'Start redigering' });
    if (await startRedigering.isVisible().catch(() => false)) await startRedigering.click();
    const enkeltdager = p.getByLabel('Enkeltdager');
    await enkeltdager.waitFor({ timeout: 3000 });
    await enkeltdager.check();
    for (const s of datoer) {
        const d = tilUkedag(parseDato(s), 1);
        const m = await finnMånedIKalender(p, d.getUTCFullYear(), d.getUTCMonth());
        await m.locator(`[data-testid^="day:${d.getUTCDate()};"]`).click();
    }
    await p.getByRole('heading', { name: 'Hva vil du endre til?' }).waitFor({ timeout: 3000 });
}

// Leser data-periode-color for én dato i kalenderen (f.eks. 'BLUE', 'BLUESTRIPED', 'BLUEOUTLINE', 'LIGHTGREENBLUE', 'GREEN', 'NONE').
export async function kalenderDagFarge(p, dato) {
    await visKalender(p);
    const d = tilUkedag(parseDato(dato), 1);
    const m = await finnMånedIKalender(p, d.getUTCFullYear(), d.getUTCMonth());
    return m.locator(`[data-testid^="day:${d.getUTCDate()};"]`).getAttribute('data-periode-color');
}

// Klikker «Fjern alt» og bekrefter i dialogen.
export async function fjernAlt(p) {
    const knapp = p.getByRole('button', { name: 'Fjern alt' }).first();
    await knapp.click();
    const modal = p.locator('#FjernAltIUttaksplanModal');
    await modal.waitFor({ timeout: 3000 });
    await modal.locator('button[data-variant="primary"], button').last().click();
    await modal.waitFor({ state: 'hidden', timeout: 5000 }).catch(() => {});
}

export async function planSomTekst(p) {
    const kalenderFane = p.getByRole('tab', { name: 'Kalender' });
    const varKalender = (await kalenderFane.count()) && (await kalenderFane.getAttribute('aria-selected')) === 'true';
    await visListe(p);
    const t = await tekstlinjer(app(p));
    const tekst = t.slice(t.findIndex((s) => s === 'Liste') + 1, t.findIndex((s) => s === 'Angre')).join(' / ');
    if (varKalender) await visKalender(p);
    return tekst;
}

export async function planInfo(p) {
    return (await tekstlinjer(app(p))).filter((s) => /uker igjen|lagt inn i planen|for mye|ligger ikke|endres automatisk|endrast automatisk/.test(s)).join(' || ');
}

// Valget vises bare når perioden overlapper og kan flyttes. «Periode uten foreldrepenger», pause og utsettelse tilbyr ikke flytting.
async function flyttResten(p, flytt = true) {
    const valg = p.getByLabel(flytt ? 'Endre og flytt resten av planen' : 'Endre uten å flytte resten av planen');
    await valg.waitFor({ timeout: 1500 }).catch(() => {});
    if (flytt && !(await valg.count())) console.log('ADVARSEL: «Endre og flytt resten av planen» ble ikke vist, resten av planen er ikke flyttet');
    if (await valg.count()) {
        await valg.check();
        await p.getByRole('button', { name: 'Legg til', exact: true }).click();
        await valg.waitFor({ state: 'hidden', timeout: 3000 }).catch(() => {});
    }
}

// Klikker «Ferdig, legg til» (liste) eller «Fortsett»/«Legg til» (kalender). Står skjemaet fortsatt åpent, kastes valideringsfeilene.
async function ferdig(p, knappNavn = /Ferdig, legg til/) {
    const knapp = p.getByRole('button', { name: knappNavn }).first();
    await knapp.click();
    await Promise.race([
        knapp.waitFor({ state: 'hidden', timeout: 3000 }),
        p.getByLabel('Endre og flytt resten av planen').waitFor({ timeout: 3000 }),
        app(p).locator('[class*=error-message]').first().waitFor({ timeout: 3000 }),
    ]).catch(() => {});
    if ((await knapp.isVisible().catch(() => false)) && !(await p.getByLabel('Endre og flytt resten av planen').count())) {
        // Aksel 8 bruker aksel-error-message, eldre versjoner navds-error-message.
        const felt = (await app(p).locator('[class*=error-message]').allInnerTexts()).filter(Boolean);
        const tekst = (await tekstlinjer(app(p))).filter((s) => /^(Du må|Dere kan|Bare en)/.test(s));
        const feil = [...new Set([...felt, ...tekst])];
        throw new Error(`Perioden ble ikke lagt til: ${feil.join(' | ')}`);
    }
}

async function åpneLeggTil(p, fom, tom) {
    await visListe(p);
    await p.getByRole('button', { name: 'Legg til periode' }).click();
    await p.getByLabel('Fra og med dato').fill(fom);
    await p.getByLabel('Til og med dato').fill(tom);
    await p.keyboard.press('Tab');
    await p.locator('input[name=hvaVilDuGjøre]').first().waitFor({ timeout: 2500 }).catch(() => {});
}

// forelder: 'MOR'|'FAR_MEDMOR'|'BEGGE'. konto: 'FELLESPERIODE'|'MØDREKVOTE'|'FEDREKVOTE'.
// BEGGE: samtidig uttak, standard mors kvote og fars kvote med 100 % til hver (prosent: { mor, far } for noe annet).
//   kontoMor og kontoFar velger kontoen for hver (f.eks. FELLESPERIODE). Låste valg (den andre forelderens vedtak) hoppes over.
// visValg: true skriver ut forelder-valgene (verdi og tekst) før valget gjøres.
// gradering: { prosent, arbeid } der arbeid er orgnr, 'SELVSTENDIG_NÆRINGSDRIVENDE' eller 'FRILANS' (standard første valg).
// morsAktivitet: teksten i listen «Hva skal mor gjøre i denne perioden?», f.eks. 'Arbeid'. overføringsårsak: f.eks. 'SYKDOM_ANNEN_FORELDER'.
// visning: 'kalender' (standard) eller 'liste' (eller VISNING=liste).
export async function leggTilPeriode(p, { fom, tom, forelder = 'MOR', konto = 'FELLESPERIODE', kontoMor = 'MØDREKVOTE', kontoFar = 'FEDREKVOTE', flytt = true, prosent = { mor: 100, far: 100 }, visValg = false, gradering, morsAktivitet, overføringsårsak, visning }) {
    const kalender = brukKalender(visning);
    if (kalender) {
        await velgDatoerIKalender(p, fom, tom);
        await p.getByRole('button', { name: /^(Ny periode|Endre)$/ }).first().click();
        await p.locator('input[name=forelder], input[name^=kontoType]').first().waitFor({ timeout: 3000 }).catch(() => {});
    } else {
        await åpneLeggTil(p, fom, tom);
        await p.getByLabel('Legge til periode med foreldrepenger').check();
    }
    if (visValg) {
        const valg = await p.locator('input[name=forelder]').evaluateAll((e) => e.map((x) => `${x.value}=${x.labels?.[0]?.innerText ?? ''}`));
        console.log('FORELDERVALG', valg.join(' | '));
    }
    // Har bare én forelder rett, spør ikke skjemaet hvem perioden gjelder.
    const forelderValg = p.locator(`input[name=forelder][value=${forelder}]`);
    if (await p.locator('input[name=forelder]').count()) await forelderValg.check();
    await p.locator('input[name^=kontoType]').first().waitFor({ timeout: 2000 }).catch(() => {});
    if (forelder === 'BEGGE') {
        for (const [navn, verdi] of [['kontoTypeMor', kontoMor], ['kontoTypeFarMedmor', kontoFar]]) {
            const valg = p.locator(`input[name=${navn}][value=${verdi}]`);
            if (await valg.isEnabled()) await valg.check();
            else console.log(`KONTO LÅST ${navn}, valgt: ${await p.locator(`input[name=${navn}]:checked`).getAttribute('value').catch(() => 'ingen')}`);
        }
        for (const [hvem, verdi] of [['mor', prosent.mor], ['far', prosent.far]]) {
            const felt = p.getByLabel(`Hvor mange prosent til ${hvem}?`);
            if (await felt.isEditable()) await felt.fill(String(verdi));
            else console.log(`PROSENT LÅST ${hvem}=${await felt.inputValue()}`);
        }
    } else {
        const kontoNavn = forelder === 'FAR_MEDMOR' ? 'kontoTypeFarMedmor' : 'kontoTypeMor';
        const kontoValg = p.locator(`input[name=${kontoNavn}][value=${konto}]`);
        await kontoValg.first().waitFor({ timeout: 2000 }).catch(() => {});
        if (await kontoValg.count()) await kontoValg.first().check().catch(() => {});
    }
    if (overføringsårsak) await p.locator('input[name=overføringsårsak]').first().waitFor({ timeout: 2000 }).catch(() => {});
    if (overføringsårsak) await p.locator(`input[name=overføringsårsak][value=${overføringsårsak}]`).check();
    // Verdi (f.eks. ARBEID) eller tekst.
    if (morsAktivitet) {
        const aktivitet = p.getByLabel('Hva skal mor gjøre i denne perioden?');
        await aktivitet.waitFor({ timeout: 2000 }).catch(() => {});
        await aktivitet.selectOption(morsAktivitet);
    }
    if (forelder === 'BEGGE') {
        const gradMor = gradering ? (gradering.mor ?? (prosent.mor < 100 ? gradering : null)) : null;
        const gradFar = gradering ? (gradering.far ?? (prosent.far < 100 ? gradering : null)) : null;
        for (const [navn, grad, feltLabel] of [
            ['skalDuKombinereArbeidOgUttakMor', gradMor, /Hvor mange prosent skal mor jobbe/],
            ['skalDuKombinereArbeidOgUttakFarMedmor', gradFar, /Hvor mange prosent skal (far|medmor) jobbe/],
        ]) {
            const radio = p.locator(`input[type=radio][name=${navn}][value=${grad ? 'true' : 'false'}]`);
            if (await radio.count()) await radio.check();
            if (grad) {
                const felt = p.getByLabel(feltLabel).first();
                await felt.waitFor({ timeout: 2000 }).catch(() => {});
                if (await felt.count()) await felt.fill(String(grad.prosent ?? 50));
            }
        }
        if (gradMor || gradFar) {
            const g = gradMor || gradFar;
            const hvor = g.arbeid ? p.locator(`input[name=hvorSkalDuJobbe][value="${g.arbeid}"]`) : p.locator('input[name=hvorSkalDuJobbe]').first();
            if (await hvor.count()) await hvor.check();
        }
    } else {
        const arbeid = p.locator(`input[type=radio][name^=skal][name*=ombinere][value=${gradering ? 'true' : 'false'}]`);
        for (let i = 0; i < (await arbeid.count()); i++) await arbeid.nth(i).check();
        if (gradering) {
            const prosentFelt = p.getByLabel(/Hvor mange prosent skal .* jobbe/).first();
            await prosentFelt.waitFor({ timeout: 2000 }).catch(() => {});
            await prosentFelt.fill(String(gradering.prosent ?? 50));
            const hvor = gradering.arbeid ? p.locator(`input[name=hvorSkalDuJobbe][value="${gradering.arbeid}"]`) : p.locator('input[name=hvorSkalDuJobbe]').first();
            if (await hvor.count()) await hvor.check();
        }
    }
    await ferdig(p, kalender ? /^(Fortsett|Legg til)$/ : /Ferdig, legg til/);
    await flyttResten(p, flytt);
}

// Valgene under «Hva vil du gjøre?» i «Legg til periode» for datoene (verdi=tekst). Lukker skjemaet etterpå.
export async function leggTilValg(p, { fom, tom }) {
    await åpneLeggTil(p, fom, tom);
    const valg = await p.locator('input[name=hvaVilDuGjøre]').evaluateAll((e) => e.map((x) => `${x.value}=${x.labels?.[0]?.innerText ?? ''}`));
    await p.getByRole('button', { name: 'Avbryt' }).first().click();
    return valg;
}

// Utsettelse («Utsettelse», bare i de første seks ukene for mor). årsak er teksten i listen: 'Sykdom', 'Innlagt' eller 'Barnet er innlagt'.
export async function leggTilUtsettelse(p, { fom, tom, årsak = 'Sykdom', flytt = false, visning }) {
    const kalender = brukKalender(visning);
    if (kalender) {
        await velgDatoerIKalender(p, fom, tom);
        await p.getByRole('button', { name: /Utsettelse/ }).first().click();
        await p.getByLabel('Velg hvorfor du skal utsette').selectOption({ label: årsak });
        await ferdig(p, /^Legg til$/);
    } else {
        await åpneLeggTil(p, fom, tom);
        await p.getByLabel('Utsettelse', { exact: true }).check();
        await p.getByLabel('Velg hvorfor du skal utsette').selectOption({ label: årsak });
        await ferdig(p);
    }
    await flyttResten(p, flytt);
}

// Pause («Pause»): valget far får etter de første seks ukene når bare han har rett. Krever mors aktivitet (tekst i listen).
export async function leggTilPause(p, { fom, tom, morsAktivitet = 'Arbeid', flytt = false, visning }) {
    const kalender = brukKalender(visning);
    if (kalender) {
        await velgDatoerIKalender(p, fom, tom);
        await p.getByRole('button', { name: 'Legg til pause' }).first().click();
        await p.getByLabel('Hva gjør mor i denne perioden?').selectOption({ label: morsAktivitet });
        await ferdig(p, /^Legg til$/);
    } else {
        await åpneLeggTil(p, fom, tom);
        await p.getByLabel('Pause', { exact: true }).check();
        // Ledeteksten er en annen enn ved uttak («Hva skal mor gjøre i denne perioden?»).
        await p.getByLabel('Hva gjør mor i denne perioden?').selectOption({ label: morsAktivitet });
        await ferdig(p);
    }
    await flyttResten(p, flytt);
}

// Periode uten foreldrepenger («Legge til periode uten foreldrepenger»).
export async function leggTilOpphold(p, { fom, tom, flytt = false }) {
    await åpneLeggTil(p, fom, tom);
    await p.getByLabel('Legge til periode uten foreldrepenger').check();
    await ferdig(p);
    await flyttResten(p, flytt);
}

export async function leggTilFerie(p, { fom, tom, forelder, flytt = true, visning }) {
    if (brukKalender(visning)) {
        await velgDatoerIKalender(p, fom, tom);
        await p.getByRole('button', { name: /^(Legg til ferie|Endre til ferie)$/ }).first().click();
        await flyttResten(p, flytt);
        return;
    }
    await åpneLeggTil(p, fom, tom);
    await p.getByLabel('Legge til ferie').check();
    if (forelder) {
        const forelderValg = p.locator(`input[name=forelder][value=${forelder}]`);
        if (await forelderValg.count()) await forelderValg.first().check().catch(() => {});
    }
    await ferdig(p);
    await flyttResten(p, flytt);
}

// Sletter dager i kalenderen ved å markere [fom, tom] og klikke søppelbøtteknappen («Slett dager fra periode»).
// Har valget flere eksisterende perioder, slettes de som matcher `velg` (regex), eller alle hvis `velg` ikke er oppgitt.
export async function slettDagerIKalender(p, fom, tom = fom, { velg } = {}) {
    await velgDatoerIKalender(p, fom, tom);
    const rader = p.locator('[data-testid^="eksisterende-periode-"]');
    await rader.first().waitFor({ timeout: 3000 });
    for (let guard = 0; guard < 10; guard++) {
        const antall = await rader.count();
        if (!antall) break;
        let klikket = false;
        for (let i = 0; i < antall; i++) {
            const rad = rader.nth(i);
            const tekst = await rad.innerText().catch(() => '');
            if (velg && !velg.test(tekst)) continue;
            const slettKnapp = rad.getByRole('button', { name: 'Slett dager fra periode' });
            if (!(await slettKnapp.count())) continue;
            await slettKnapp.click();
            const tilbakeITid = p.getByRole('dialog').filter({ hasText: 'tilbake i tid' });
            await Promise.race([
                tilbakeITid.waitFor({ timeout: 1500 }),
                rad.waitFor({ state: 'detached', timeout: 1500 }),
            ]).catch(() => {});
            if (await tilbakeITid.count()) {
                await tilbakeITid.getByRole('button', { name: 'Slett', exact: true }).click();
                await tilbakeITid.waitFor({ state: 'hidden', timeout: 5000 }).catch(() => {});
            }
            klikket = true;
            break;
        }
        if (!klikket) break;
    }
}

// Sletter periode. Hvis `tom` oppgis eller `fra` er dato ('dd.mm.åååå' / 'åååå-mm-dd'), slettes direkte i kalenderen.
// Ellers slettes raden i listevisningen hvis radtekst starter med `fra` (f.eks. '24. nov. 26').
export async function slettPeriode(p, fra, { tom, velg, visning } = {}) {
    const erDato = /^(\d{1,2}\.\d{1,2}\.\d{4}|\d{4}-\d{1,2}-\d{1,2})$/.test(String(fra).trim());
    if (brukKalender(visning) && (tom || erDato)) {
        return slettDagerIKalender(p, fra, tom ?? fra, { velg });
    }
    await visListe(p);
    await p.getByRole('button', { name: new RegExp(`^${fra.replace(/\./g, '\\.')}`) }).first().click();
    const slett = p.getByRole('button', { name: 'Slett', exact: true }).first();
    await slett.waitFor({ timeout: 3000 }).catch(() => {});
    await slett.click();
    // Sletting tilbake i tid må bekreftes i en dialog («Er du sikker på at du skal slette foreldrepenger tilbake i tid?»).
    const tilbakeITid = p.getByRole('dialog').filter({ hasText: 'tilbake i tid' });
    const slettValgte = p.getByRole('button', { name: 'Slett valgte perioder' });
    await Promise.race([
        tilbakeITid.waitFor({ timeout: 3000 }),
        slettValgte.waitFor({ timeout: 3000 }),
        slett.waitFor({ state: 'hidden', timeout: 3000 }),
        app(p).locator('[class*=error-message]').first().waitFor({ timeout: 3000 }),
    ]).catch(() => {});
    if (await tilbakeITid.count()) {
        const bekreft = tilbakeITid.getByRole('button', { name: 'Slett', exact: true });
        await bekreft.click();
        await Promise.race([
            tilbakeITid.waitFor({ state: 'hidden', timeout: 5000 }),
            app(p).locator('[class*=error-message]').first().waitFor({ timeout: 5000 }),
        ]).catch(() => {});
    }
    if (!(await slettValgte.count())) return;
    const valg = p.locator('input[type=checkbox]');
    const navn = await valg.evaluateAll((e) => e.map((x) => x.labels?.[0]?.innerText ?? ''));
    if (!velg) throw new Error(`Raden ${fra} har flere perioder, send med velg: ${navn.join(' | ')}`);
    for (let i = 0; i < navn.length; i++) if (velg.test(navn[i])) await valg.nth(i).check();
    await slettValgte.first().click();
    await Promise.race([
        slettValgte.first().waitFor({ state: 'hidden', timeout: 5000 }),
        app(p).locator('[class*=error-message]').first().waitFor({ timeout: 5000 }),
    ]).catch(() => {});
}

export async function nesteSteg(p) {
    const forrige = steg(p);
    const gammelForm = await app(p).locator('form').first().elementHandle().catch(() => null);
    await p.getByRole('button', { name: 'Neste steg' }).click();
    await ventPåStegEndring(p, forrige, gammelForm);
    return steg(p);
}

// Sletter søkerens del av raden og legger inn ferie på datoene (fom/tom 'dd.mm.åååå') uten å flytte resten.
// Brukes der «Endre» ikke tilbyr ferie, f.eks. samtidig uttak rundt fødsel.
export async function erstattMedFerie(p, fra, { fom, tom, forelder }) {
    await slettPeriode(p, fra);
    await leggTilFerie(p, { fom, tom, forelder, flytt: false });
}

// Åpner raden som starter med `fra`, klikker «Endre» og gir perioden nye datoer (fom/tom 'dd.mm.åååå').
// Raden må ha én delperiode. flytt: true velger «Endre og flytt resten av planen».
export async function endrePeriode(p, fra, { fom, tom, flytt = false }) {
    await visListe(p);
    await p.getByRole('button', { name: new RegExp(`^${fra.replace(/\./g, '\\.')}`) }).first().click();
    const endre = p.getByRole('button', { name: 'Endre', exact: true }).first();
    await endre.waitFor({ timeout: 3000 }).catch(() => {});
    await endre.click();
    const fraFelt = p.getByLabel('Fra og med dato').first();
    await fraFelt.waitFor({ timeout: 3000 }).catch(() => {});
    await fraFelt.fill(fom);
    await p.getByLabel('Til og med dato').first().fill(tom);
    await p.keyboard.press('Tab');
    const medFp = p.getByLabel('Periode med foreldrepenger');
    if (await medFp.count()) await medFp.first().check().catch(() => {});
    const knapp = p.getByRole('button', { name: /Ferdig, legg til/ });
    await knapp.click();
    await Promise.race([
        knapp.waitFor({ state: 'hidden', timeout: 5000 }),
        app(p).locator('[class*=error-message]').first().waitFor({ timeout: 5000 }),
    ]).catch(() => {});
    if (await knapp.isVisible().catch(() => false)) {
        const feil = await p.locator('#app [class*=error-message]').allInnerTexts();
        throw new Error(`«Endre» på raden ${fra} ble ikke lagret: ${feil.join(' | ')}`);
    }
    const valg = p.getByLabel(flytt ? 'Endre og flytt resten av planen' : 'Endre uten å flytte resten av planen');
    await valg.waitFor({ timeout: 2000 }).catch(() => {});
    if (await valg.count()) {
        await valg.check();
        const leggTil = p.getByRole('button', { name: 'Legg til', exact: true });
        await leggTil.click();
        await valg.waitFor({ state: 'hidden', timeout: 3000 }).catch(() => {});
    }
}

// Åpner raden som starter med `fra`, klikker «Endre» og gjør perioden om til ferie.
export async function endreTilFerie(p, fra) {
    await visListe(p);
    await p.getByRole('button', { name: new RegExp(`^${fra.replace(/\./g, '\\.')}`) }).first().click();
    const endre = p.getByRole('button', { name: 'Endre', exact: true }).first();
    await endre.waitFor({ timeout: 3000 }).catch(() => {});
    await endre.click();
    const ferie = p.locator('input[name=hvaVilDuGjøre][value=LEGG_TIL_FERIE]');
    await ferie.waitFor({ timeout: 3000 }).catch(() => {});
    if (!(await ferie.count()))
        throw new Error(`«Endre» på raden ${fra} har ikke valget ferie (f.eks. samtidig uttak). Bruk erstattMedFerie.`);
    await ferie.check();
    const lagre = p.getByRole('button', { name: /Ferdig|Oppdater|Lagre/ }).first();
    await lagre.click();
    await Promise.race([
        lagre.waitFor({ state: 'hidden', timeout: 5000 }),
        app(p).locator('[class*=error-message]').first().waitFor({ timeout: 5000 }),
    ]).catch(() => {});
    await flyttResten(p, false);
}
