package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.kontrakter.risk.kodeverk.RisikoklasseType;

// TODO: Fiks opp i testbasene
public abstract class VerdikjedeTestBase extends FpsakTestBase {

    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandling(Saksnummer saksnummer,
                                                                   boolean revurdering,
                                                                   boolean tilbakekreving) {
        foreslårOgFatterVedtakVenterTilAvsluttetBehandling(saksnummer, revurdering, tilbakekreving, true);
    }

    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandling(Saksnummer saksnummer, boolean revurdering,
                                                                   boolean tilbakekreving, boolean ventPåSendtBrev) {
        if (!revurdering) {
            saksbehandler.ventTilRisikoKlassefiseringsstatus(RisikoklasseType.IKKE_HØY);
        }
        saksbehandler.bekreftAksjonspunkt(new ForeslåVedtakBekreftelse());

        beslutter.hentFagsak(saksnummer);
        if (beslutter.harRevurderingBehandling() && revurdering) {
            beslutter.ventPåOgVelgRevurderingBehandling();
        }
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(new FatterVedtakBekreftelse());
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        if (tilbakekreving) {
            beslutter.bekreftAksjonspunkt(bekreftelse);
            beslutter.ventTilAvsluttetBehandlingOgDetOpprettesTilbakekreving();
        } else {
            beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        }
        if (ventPåSendtBrev) {
            saksbehandler.ventTilHistorikkinnslag(HistorikkType.BREV_SENDT);
        }
    }

}
