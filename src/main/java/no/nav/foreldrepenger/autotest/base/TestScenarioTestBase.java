package no.nav.foreldrepenger.autotest.base;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioKlient;
import no.nav.foreldrepenger.autotest.util.http.BasicHttpSession;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public abstract class TestScenarioTestBase extends TestBase {

    protected TestscenarioKlient testscenarioKlient;

    public TestScenarioTestBase() {
        testscenarioKlient = new TestscenarioKlient(BasicHttpSession.session());
    }

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i Autotest")
    protected TestscenarioDto opprettTestscenarioMedPrivatArbeidsgiver(String id, String aktorId) {
        Object testscenarioObject = testscenarioRepositoryImpl.hentScenario(id);
        return testscenarioKlient.opprettTestscenarioMedAktorId(id, testscenarioObject, aktorId);
    }

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i Autotest")
    protected TestscenarioDto opprettTestscenario(String id) {
        Object testscenarioObject = testscenarioRepositoryImpl.hentScenario(id);
        return testscenarioKlient.opprettTestscenario(id, testscenarioObject);
    }
}
