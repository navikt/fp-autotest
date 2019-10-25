package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.aktoerer.fpoppdrag.Saksbehandler;
import no.nav.foreldrepenger.autotest.klienter.vtp.scenario.TestscenarioKlient;
import no.nav.foreldrepenger.autotest.util.http.BasicHttpSession;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingErketype;

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
