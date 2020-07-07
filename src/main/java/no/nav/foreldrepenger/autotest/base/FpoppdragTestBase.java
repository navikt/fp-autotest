package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.aktoerer.fpoppdrag.Saksbehandler;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingErketype;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioKlient;
import no.nav.foreldrepenger.autotest.util.http.BasicHttpSession;

public class FpoppdragTestBase extends TestBase {

    protected Saksbehandler saksbehandler;
    protected TestscenarioKlient testscenarioKlient;

    protected InntektsmeldingErketype inntektsmeldingErketype;

    public FpoppdragTestBase() {
        saksbehandler = new Saksbehandler();
        testscenarioKlient = new TestscenarioKlient(BasicHttpSession.session());
        inntektsmeldingErketype = new InntektsmeldingErketype();
    }

}
