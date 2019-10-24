package no.nav.foreldrepenger.autotest.base;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.expect.ExpectKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioFraAutotestKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioKlient;
import no.nav.foreldrepenger.autotest.util.http.BasicHttpSession;
import no.nav.foreldrepenger.autotest.util.testscenario.TestscenarioReader;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

import java.io.IOException;

public abstract class TestScenarioTestBase extends TestBase {

    protected TestscenarioKlient testscenarioKlient;
    protected TestscenarioFraAutotestKlient testscenarioFraAutotestKlient;
    protected TestscenarioReader testscenarioReader;
    protected ExpectKlient expectKlient;

    public TestScenarioTestBase() {
        testscenarioKlient = new TestscenarioKlient(BasicHttpSession.session());
        testscenarioFraAutotestKlient = new TestscenarioFraAutotestKlient(BasicHttpSession.session());
        testscenarioReader = new TestscenarioReader();
        expectKlient = new ExpectKlient(BasicHttpSession.session());
    }

    @Step("Oppretter testscenario {id}")
    protected TestscenarioDto opprettScenario(String id) throws IOException {
        return testscenarioKlient.opprettTestscenario(id);
    }

    @Step("Oppretter testscenario {id}")
    protected TestscenarioDto opprettScenarioMedPrivatArbeidsgiver(String id, String aktorId) throws IOException {
        return testscenarioKlient.opprettTestscenarioMedAktorId(id, aktorId);
    }

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i autotest")
    protected TestscenarioDto initialiserScenario(String id) throws IOException {
        Object testscenarioObject = testscenarioReader.LesOgReturnerScenarioFraJsonfil(id);
        return (testscenarioObject == null ? null : testscenarioFraAutotestKlient.initialiserTestscenario(id, testscenarioObject));
    }
}
