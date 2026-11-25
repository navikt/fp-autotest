// Løser «Kontroller dokumentasjon» (uttaksdokumentasjon) og viser status etterpå.
// node run.mjs scenarier/los-dokumentasjon.mjs <sak> [Godkjent|'Ikke godkjent'|'Mangler dokumentasjon']
import * as F from '../lib/fpsak.mjs';

export default async ({ fpsak, args }) => {
    const [sak, vurdering = 'Godkjent'] = args;
    const p = await fpsak(sak);
    await F.velgBehandling(p, 'Revurdering');
    console.log(sak, 'FØR  ', JSON.stringify(await F.status(p)));
    if ((await F.status(p)).aksjonspunkt.some((a) => a.startsWith('Kontroller dokumentasjon'))) {
        await F.kontrollerDokumentasjon(p, { vurdering });
        await F.lastPåNytt(p);
    }
    console.log(sak, 'ETTER', JSON.stringify(await F.status(p)));
};
