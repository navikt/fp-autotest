// Endringssøknad der søkeren legger til én periode. Viser planen, forelder-valgene i skjemaet, oppsummering og ev. fpsak.
// node run.mjs scenarier/endring-legg-til.mjs <familie> <mor|far|medfar> <saksnummer> <fom> <tom> [konto] [send]
// FORELDER=MOR|FAR_MEDMOR velger verdien i forelder-feltet (standard: MOR for mor, ellers FAR_MEDMOR).
import * as S from '../lib/soknad.mjs';
import * as F from '../lib/fpsak.mjs';

export default async ({ som, fpsak, familie, args }) => {
    const [navn, hvem, saksnummer, fom, tom, konto = 'FELLESPERIODE', send] = args;
    const p = await som(`${navn}-${hvem}-endring`, 'soknad', familie(navn)[hvem]);
    await S.slettUtkast(p);
    await S.start(p);
    console.log('FØR  ', await S.planSomTekst(p));
    const forelder = process.env.FORELDER ?? (hvem === 'mor' ? 'MOR' : 'FAR_MEDMOR');
    console.log('VELGER', forelder);
    await S.leggTilPeriode(p, { fom, tom, konto, forelder, flytt: false, visValg: true });
    console.log('ETTER', await S.planSomTekst(p));
    console.log('INFO ', await S.planInfo(p));
    const neste = await S.nesteSteg(p);
    if (neste !== 'oppsummering') {
        const { tekstlinjer } = await import('../lib/browser.mjs');
        return console.log('Kom ikke til oppsummering:', neste, (await tekstlinjer(p.locator('#app'))).filter((s) => /må|kan ikke|feil|minst/i.test(s)).join(' | '));
    }
    console.log('OPPSUMMERING', await S.oppsummeringPlan(p));
    if (send !== 'send') return;
    await S.sendInn(p);
    const fs = await fpsak(saksnummer);
    const r = await F.ventPåBehandling(fs, 'Revurdering');
    console.log('FPSAK', JSON.stringify(r.status));
    console.log('UTTAK', JSON.stringify((await F.uttakFraApi(fs, 'Revurdering')).søker));
};
