// Uttaksresultatet fra fpsak-API-et (uttak/resultat-perioder) for nyeste behandling av typen, eller RAW=1 for hele svaret.
// node run.mjs scenarier/uttak-api.mjs <sak> [sak ...]     BEHANDLING=Førstegangsbehandling (standard Revurdering, faller tilbake til førstegangsbehandling), BEHANDLING_NR=1 gir nest nyeste
import * as F from '../lib/fpsak.mjs';

export default async ({ fpsak, args }) => {
    for (const sak of args) {
        const p = await fpsak(sak);
        const r = await F.uttakFraApi(p, process.env.BEHANDLING ?? 'Revurdering', { nr: Number(process.env.BEHANDLING_NR ?? 0) });
        if (process.env.RAW === '1') {
            console.log('RAW', sak, JSON.stringify(r.dto).slice(0, Number(process.env.MAKS ?? 6000)));
            continue;
        }
        console.log(`=== ${sak} ${r.type} ${r.status}`);
        for (const [hvem, perioder] of [['SØKER', r.søker], ['ANNEN', r.annen]]) for (const x of perioder) console.log(hvem, x);
    }
};
