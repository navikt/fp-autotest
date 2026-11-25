// Skriver ut status og plan i oversikten og fpsak for en sak.
// node run.mjs scenarier/sjekk-sak.mjs <familienavn> <mor|far> <saksnummer>
import * as O from '../lib/oversikt.mjs';
import * as F from '../lib/fpsak.mjs';

export default async ({ som, fpsak, familie, args }) => {
    const [navn, hvem, saksnummer] = args;
    const fam = familie(navn);
    const p = await som(`${navn}-${hvem}-oversikt`, 'oversikt', fam[hvem]);
    console.log('OVERSIKT', JSON.stringify(await O.sak(p, saksnummer), null, 1));

    const fs = await fpsak(saksnummer);
    await F.velgBehandling(fs, 'Revurdering');
    console.log('FPSAK', JSON.stringify(await F.status(fs)));
};
