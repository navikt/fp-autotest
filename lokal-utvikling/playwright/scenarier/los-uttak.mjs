// Løser uttak-aksjonspunktet for valgt periode, sender til godkjenning og godkjenner som Birger Beslutter.
// node run.mjs scenarier/los-uttak.mjs <sak> [konto] [årsak-regex] [ikke-oppfylt]
// Standard: konto Foreldrepenger, oppfylt, «kun far har rett - dager uten aktivitetskrav», utbetalingsgrad 100.
// Hopper over steg som allerede er gjort, så scenarioet kan kjøres på nytt.
import * as F from '../lib/fpsak.mjs';

export default async ({ fpsak, args }) => {
    const [sak, konto = 'Foreldrepenger', årsak = 'dager uten aktivitetskrav', ikke] = args;
    const oppfylt = ikke !== 'ikke-oppfylt';
    const p = await fpsak(sak);
    await F.velgBehandling(p, 'Revurdering');
    let s = await F.status(p);
    console.log(sak, 'FØR  ', JSON.stringify(s));
    if (s.aksjonspunkt.some((a) => a.startsWith('Ikke gyldig grunn'))) {
        await F.løsUttakPeriode(p, { konto, oppfylt, årsak: new RegExp(årsak), utbetalingsgrad: oppfylt ? '100' : undefined });
        await p.reload({ waitUntil: 'networkidle' });
        await p.waitForTimeout(2000);
    }
    if (await p.getByRole('button', { name: 'Til godkjenning' }).count()) await F.tilGodkjenning(p);
    const b = await fpsak(sak, { bruker: 'Birger Beslutter' });
    await F.velgBehandling(b, 'Revurdering');
    if (await b.getByRole('tab', { name: 'Godkjenning' }).count()) await F.godkjennVedtak(b);
    await b.reload({ waitUntil: 'networkidle' });
    await b.waitForTimeout(1500);
    console.log(sak, 'ETTER', JSON.stringify(await F.status(b)));
};
