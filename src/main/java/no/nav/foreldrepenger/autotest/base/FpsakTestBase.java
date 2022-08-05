package no.nav.foreldrepenger.autotest.base;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.Fordel;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.SøknadMottak;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.util.log.LoggFormater;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Søker;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.kontrakter.risk.kodeverk.RisikoklasseType;

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


    @BeforeEach
    public void setUp() {
        innsender = new SøknadMottak();
        fordel = new Fordel();
        // TODO: Gjør om til å bruke et felles objekt med saksbehandling. Logg inn for hver type.
        saksbehandler = new Saksbehandler(Aktoer.Rolle.SAKSBEHANDLER);
        overstyrer = new Saksbehandler(Aktoer.Rolle.OVERSTYRER);
        beslutter = new Saksbehandler(Aktoer.Rolle.BESLUTTER);
        klagebehandler = new Saksbehandler(Aktoer.Rolle.KLAGEBEHANDLER);
        LoggFormater.leggTilKjørendeTestCaseILogger();
    }

    public void foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(Saksnummer saksnummer,
                                                                                         boolean revurdering) {
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
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        if (saksbehandler.harHistorikkinnslagPåBehandling(HistorikkinnslagType.BREV_BESTILT)) {
            saksbehandler.ventTilHistorikkinnslag(HistorikkinnslagType.BREV_SENT);
        }
    }

    // TODO FLYTT TIL SØKNAD
    protected NorskForelder lagNorskAnnenforeldre(Søker annenpart) {
        when(fordel.oppslag.aktørId(annenpart.fødselsnummer())).thenReturn(annenpart.aktørId());
        return new NorskForelder(annenpart.fødselsnummer(), "");
    }
}
