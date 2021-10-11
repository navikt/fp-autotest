package no.nav.foreldrepenger.autotest.base;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.fordel.Fordel;
import no.nav.foreldrepenger.autotest.aktoerer.foreldrepenger.Saksbehandler;
import no.nav.foreldrepenger.autotest.aktoerer.fptilbake.TilbakekrevingSaksbehandler;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.SøknadMottak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.util.log.LoggFormater;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Søker;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.NorskForelder;

public abstract class FpsakTestBase {

    /*
     * Aktører
     */
    protected SøknadMottak innsender;
    protected Fordel fordel;
    protected Saksbehandler saksbehandler;
    protected Saksbehandler overstyrer;
    protected Saksbehandler beslutter;
    protected Saksbehandler klagebehandler;
    protected TilbakekrevingSaksbehandler tbksaksbehandler;


    @BeforeEach
    public void setUp() {
        innsender = new SøknadMottak(Aktoer.Rolle.SAKSBEHANDLER);
        fordel = new Fordel(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler = new Saksbehandler(Aktoer.Rolle.SAKSBEHANDLER);
        overstyrer = new Saksbehandler(Aktoer.Rolle.OVERSTYRER);
        beslutter = new Saksbehandler(Aktoer.Rolle.BESLUTTER);
        klagebehandler = new Saksbehandler(Aktoer.Rolle.KLAGEBEHANDLER);
        tbksaksbehandler = new TilbakekrevingSaksbehandler(Aktoer.Rolle.SAKSBEHANDLER);
        LoggFormater.leggTilKjørendeTestCaseILogger();
    }

    public Familie nyFamilie(String ID) {
        return new Familie(ID, fordel);
    }


    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(long saksnummer,
                                                                                         boolean revurdering) {
        if (!revurdering) {
            saksbehandler.ventTilRisikoKlassefiseringsstatus("IKKE_HOY");
        }
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

         beslutter.hentFagsak(saksnummer);
        if (beslutter.harRevurderingBehandling() && revurdering) {
            beslutter.ventPåOgVelgRevurderingBehandling();
        }
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        if (saksbehandler.harHistorikkinnslagForBehandling(HistorikkinnslagType.BREV_BESTILT,
                saksbehandler.valgtBehandling.uuid)) {
            saksbehandler.ventTilHistorikkinnslag(HistorikkinnslagType.BREV_SENT);
        }
    }

    // TODO FLYTT TIL SØKNAD
    protected NorskForelder lagNorskAnnenforeldre(Søker annenpart) {
        when(fordel.oppslag.aktørId(annenpart.fødselsnummer())).thenReturn(annenpart.aktørId());
        return new NorskForelder(annenpart.fødselsnummer(), "");
    }
}
