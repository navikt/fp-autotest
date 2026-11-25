// Skriver status for saker i fpsak og løser enkle steg: «Vurder faresignalene» (ikke reelle),
// «annen forelder har rett» og «Fatt vedtak». Gir stegene «Til godkjenning», godkjenner Birger Beslutter.
// node run.mjs scenarier/behandle.mjs <saksnummer> [saksnummer ...]
// TIL_VEDTAK=1 bruker F.behandleTilVedtak i stedet (løser også «Kontroller dokumentasjon», 5074). BEHANDLING=Førstegangsbehandling velger type (standard Revurdering).
import * as F from '../lib/fpsak.mjs';

export default async ({ fpsak, args }) => {
    if (process.env.TIL_VEDTAK === '1') {
        for (const sak of args) {
            const r = await F.behandleTilVedtak(fpsak, sak, { type: process.env.BEHANDLING ?? 'Revurdering' });
            console.log(sak, 'RESULTAT', r?.grunn, JSON.stringify(r?.status));
        }
        return;
    }
    for (const sak of args) {
        const p = await fpsak(sak);
        await F.velgBehandling(p, 'Revurdering');
        let s = await F.status(p);
        console.log(sak, 'FØR  ', JSON.stringify(s));
        if (s.aksjonspunkt.some((a) => a.startsWith('Vurder faresignalene'))) {
            await F.vurderFaresignaler(p);
            await F.lastPåNytt(p);
            s = await F.status(p);
        }
        if (s.aksjonspunkt.some((a) => a.startsWith('Vurder om den andre forelderen har rett'))) {
            await F.bekreftAnnenForelderHarRett(p);
            await F.lastPåNytt(p);
            s = await F.status(p);
        }
        if (s.kanFatte) await F.fattVedtak(p);
        else if (await p.getByRole('button', { name: 'Til godkjenning' }).count()) {
            await F.tilGodkjenning(p);
            const b = await fpsak(sak, { bruker: 'Birger Beslutter' });
            await F.velgBehandling(b, 'Revurdering');
            await F.godkjennVedtak(b);
        }
        await F.lastPåNytt(p);
        s = await F.status(p);
        if (s.sak?.includes('Under behandling')) {
            await p.getByText(/Løpende|Avsluttet/).first().waitFor({ timeout: 5000 }).catch(() => {});
            s = await F.status(p);
        }
        console.log(sak, 'ETTER', JSON.stringify(s));
    }
};
