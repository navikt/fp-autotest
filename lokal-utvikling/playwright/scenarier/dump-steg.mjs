// Dumper teksten og feltene på ett steg i søknaden for far/mor/medfar.
// node run.mjs scenarier/dump-steg.mjs <familie> <mor|far|medfar> <steg> [nytt]
// SITUASJON=adopsjon velger situasjon på første steg, BARN=nytt velger «nytt barn» på forsiden. BILDE=1 tar skjermbilde før dump. SETT='navn=verdi,klikk:Knappetekst,...' fyller ut felt og klikker knapper før dump, VELG og NESTE se under.
import * as S from '../lib/soknad.mjs';
import { tekstlinjer, fyllFelt, skjermbilde } from '../lib/browser.mjs';

export default async ({ som, familie, args }) => {
    const [navn, hvem, steg, nytt] = args;
    const fam = familie(navn);
    const annen = hvem === 'medfar' ? 'far' : hvem === 'far' ? (fam.medfar ? 'medfar' : 'mor') : 'far';
    const p = await som(`${navn}-${hvem}-vite`, 'soknad', fam[hvem]);
    if (nytt) await S.slettUtkast(p);
    await S.start(p, { barn: process.env.BARN ?? 0 });
    await S.fyllUt(p, { rolle: hvem === 'mor' ? 'mor' : 'far', termin: fam.fødselsdato ? undefined : fam.termindato, fødselTermin: fam.fødselsdato, annenPart: { fnr: fam[annen] }, stoppVed: steg, dekningsgrad: process.env.DG, situasjon: process.env.SITUASJON });
    await fyllFelt(p, process.env.SETT);
    if (process.env.VELG) { // f.eks. VELG=dekningsgrad=100: velger og trykker Neste steg før dump
        const [n, v] = process.env.VELG.split('=');
        await p.locator(`input[name="${n}"][value="${v}"]`).check();
        if (process.env.NESTE) { await S.nesteSteg(p); console.log('URL', p.url()); }
    }
    if (process.env.BILDE) console.log('BILDE', await skjermbilde(p, `${navn}-${hvem}-${steg}`));
    console.log('TEKST', (await tekstlinjer(p.locator('#app'))).join(' / '));
    // Varsler og modaler i Aksel-portaler ligger utenfor #app og kan ligge over siden.
    const portal = (await p.locator('[data-aksel-portal]').allInnerTexts()).map((s) => s.trim()).filter(Boolean);
    if (portal.length) console.log('PORTAL', portal.join(' / ').replace(/\s*\n+\s*/g, ' / '));
    console.log('FELT', JSON.stringify(await p.locator('#app input, #app select, #app textarea').evaluateAll((e) => e.map((x) => `${x.name}=${x.type === 'radio' || x.type === 'checkbox' ? x.value : '«' + x.value + '»'}${x.checked ? '*' : ''}${x.disabled ? '(låst)' : ''}${x.type === 'file' ? '(fil)' : ''}`))));
};
