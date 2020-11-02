package no.nav.foreldrepenger.autotest.base;

import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.fordel.Fordel;
import no.nav.foreldrepenger.autotest.aktoerer.foreldrepenger.Saksbehandler;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;

public class FpsakTestBase extends TestScenarioTestBase {

    /*
     * Aktører
     */
    protected Fordel fordel;
    protected Saksbehandler saksbehandler;
    protected Saksbehandler overstyrer;
    protected Saksbehandler beslutter;
    protected Saksbehandler klagebehandler;

    @BeforeEach
    public void setUp() {
        fordel = new Fordel();
        saksbehandler = new Saksbehandler();
        overstyrer = new Saksbehandler();
        beslutter = new Saksbehandler();
        klagebehandler = new Saksbehandler();
    }




    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(long saksnummer,
                                                                                         boolean revurdering) {
        if (!revurdering) {
            saksbehandler.ventTilRisikoKlassefiseringsstatus("IKKE_HOY");
        }
        foreslåOgFatteVedtakOgVentTilAvsluttet(saksnummer, revurdering);
        if (saksbehandler.harHistorikkinnslagForBehandling(HistorikkInnslag.BREV_BESTILT,
                saksbehandler.valgtBehandling.id)) {
            saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.BREV_SENDT);
        }
    }

    public void foreslåOgFatteVedtakOgVentTilAvsluttet(long saksnummer, boolean revurdering) {
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        if (beslutter.harRevurderingBehandling() && revurdering) {
            beslutter.ventPåOgVelgRevurderingBehandling();
        }
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }
}
