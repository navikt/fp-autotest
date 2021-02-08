package no.nav.foreldrepenger.autotest.base;

import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.fordel.Fordel;
import no.nav.foreldrepenger.autotest.aktoerer.foreldrepenger.Saksbehandler;
import no.nav.foreldrepenger.autotest.aktoerer.fptilbake.TilbakekrevingSaksbehandler;
import no.nav.foreldrepenger.autotest.aktoerer.inntektsmelding.Innsender;
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
    protected TilbakekrevingSaksbehandler tbksaksbehandler;
    protected Innsender innsender;

    @BeforeEach
    public void setUp() {
        log.info("Setup fpsakTestBase");
        fordel = new Fordel();
        saksbehandler = new Saksbehandler();
        overstyrer = new Saksbehandler();
        beslutter = new Saksbehandler();
        klagebehandler = new Saksbehandler();
        tbksaksbehandler = new TilbakekrevingSaksbehandler();
        innsender = new Innsender();
    }




    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(long saksnummer,
                                                                                         boolean revurdering) {
        if (!revurdering) {
            saksbehandler.ventTilRisikoKlassefiseringsstatus("IKKE_HOY");
        }
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        if (beslutter.harRevurderingBehandling() && revurdering) {
            beslutter.ventPåOgVelgRevurderingBehandling();
        }
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        if (saksbehandler.harHistorikkinnslagForBehandling(HistorikkInnslag.BREV_BESTILT,
                saksbehandler.valgtBehandling.id)) {
            saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.BREV_SENDT);
        }
    }
}
