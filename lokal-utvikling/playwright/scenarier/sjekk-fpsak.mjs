// Status og uttak i fpsak. node run.mjs scenarier/sjekk-fpsak.mjs <saksnummer>
// Skriver ut endringsdatoen fra uttak/resultat-perioder (grunnlaget for pinnen
// «Startpunkt for revurdering» i uttaksbildet) og teksten i pinnene i tidslinjen.
import * as F from '../lib/fpsak.mjs';

export default async ({ fpsak, args }) => {
    const [sak] = args;
    const p = await fpsak(sak);
    let resultatUrl;
    p.on('response', (r) => {
        if (r.url().includes('/behandling/uttak/resultat-perioder')) resultatUrl = r.url();
    });
    await F.lastPåNytt(p);
    await F.velgBehandling(p, 'Revurdering', { nr: Number(process.env.BEHANDLING_NR ?? 0) });
    console.log('FPSAK', JSON.stringify(await F.status(p)));
    console.log('UTTAK', await F.uttak(p));
    console.log('UTTAKSPERIODER', JSON.stringify(await F.uttakPerioder(p)));
    if (resultatUrl) {
        const dto = await (await p.context().request.get(resultatUrl)).json().catch(() => ({}));
        console.log('FPSAK_ENDRINGSDATO', dto.endringsdato ?? 'mangler');
    } else console.log('FPSAK_ENDRINGSDATO ingen kall til resultat-perioder');
    console.log('PINNER', JSON.stringify(await F.tidslinjePinner(p)));
};
