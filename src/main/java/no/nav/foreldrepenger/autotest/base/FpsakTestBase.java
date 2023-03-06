package no.nav.foreldrepenger.autotest.base;

import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Beslutter;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Klagebehandler;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Overstyrer;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
import no.nav.foreldrepenger.autotest.util.log.LoggFormater;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Søker;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.NorskForelder;

// TODO: Fiks opp i testbasene
public abstract class FpsakTestBase {

    /*
     * Aktører
     */
    protected Saksbehandler saksbehandler;
    protected Overstyrer overstyrer;
    protected Beslutter beslutter;
    protected Klagebehandler klagebehandler;


    @BeforeEach
    public void setUp() {
        saksbehandler = new Saksbehandler();
        overstyrer = new Overstyrer();
        beslutter = new Beslutter();
        klagebehandler = new Klagebehandler();
        LoggFormater.leggTilKjørendeTestCaseILogger();
    }

    protected NorskForelder lagNorskAnnenforeldre(Søker annenpart) {
        return new NorskForelder(annenpart.fødselsnummer(), "");
    }
}
