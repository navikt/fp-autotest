// Endringssøknad der søkeren sletter perioden som starter med <radstart>. Viser plan og ev. fpsak.
// Handling: slett (standard) eller HANDLING=ferie (bytter perioden ut med ferie). For samtidig uttak: HANDLING=ferie FERIE=fom-tom.
// node run.mjs scenarier/endring-slett-periode.mjs <familienavn> <mor|far> '<radstart>' <saksnummer> [send|nosend] [ny periode: fom-tom-konto]
import * as S from '../lib/soknad.mjs';
import * as F from '../lib/fpsak.mjs';

export default async ({ som, fpsak, familie, args }) => {
    const [navn, hvem, rad, saksnummer, send, ny] = args;
    const p = await som(`${navn}-${hvem}-endring`, 'soknad', familie(navn)[hvem]);
    await S.slettUtkast(p);
    await S.start(p);
    console.log('FØR  ', await S.planSomTekst(p));
    if (process.env.HANDLING === 'ferie' && process.env.FERIE) {
        const [fom, tom] = process.env.FERIE.split('-');
        await S.erstattMedFerie(p, rad, { fom, tom, forelder: hvem === 'far' ? 'FAR_MEDMOR' : 'MOR' });
    } else if (process.env.HANDLING === 'ferie') await S.endreTilFerie(p, rad);
    else await S.slettPeriode(p, rad);
    if (ny) {
        const [fom, tom, konto] = ny.split('-');
        await S.leggTilPeriode(p, { fom, tom, konto, forelder: hvem === 'far' ? 'FAR_MEDMOR' : 'MOR' });
    }
    console.log('ETTER', await S.planSomTekst(p));
    console.log('INFO ', await S.planInfo(p));
    const neste = await S.nesteSteg(p);
    if (neste !== 'oppsummering') {
        const { tekstlinjer } = await import('../lib/browser.mjs');
        return console.log('Kom ikke til oppsummering:', neste, (await tekstlinjer(p.locator('#app'))).filter((s) => /må|kan ikke|feil|minst/i.test(s)).join(' | '));
    }
    if (send !== 'send') return console.log('OPPSUMMERING', await S.oppsummeringPlan(p));
    await S.sendInn(p);
    const fs = await fpsak(saksnummer);
    const r = await F.ventPåBehandling(fs, 'Revurdering');
    console.log('FPSAK', JSON.stringify(r.status));
    console.log('UTTAK', JSON.stringify((await F.uttakFraApi(fs, 'Revurdering')).søker));
};
