package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.kontrakter.risk.kodeverk.RisikoklasseType;

// TODO: Fiks opp i testbasene
public abstract class VerdikjedeTestBase extends FpsakTestBase {

    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(Saksnummer saksnummer, boolean revurdering,
                                                                                         boolean tilbakekreving) {
        if (!revurdering) {
            saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        }
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        if (beslutter.harRevurderingBehandling() && revurdering) {
            beslutter.ventPåOgVelgRevurderingBehandling();
        }
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        if (tilbakekreving) {
            beslutter.bekreftAksjonspunkt(bekreftelse);
            beslutter.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        } else {
            beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        }
        if (saksbehandler.harHistorikkinnslagPåBehandling(HistorikkinnslagType.BREV_BESTILT)) {
            saksbehandler.ventTilHistorikkinnslag(HistorikkinnslagType.BREV_SENT);
        }
    }

}
