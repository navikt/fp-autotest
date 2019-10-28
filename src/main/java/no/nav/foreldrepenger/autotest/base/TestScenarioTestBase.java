package no.nav.foreldrepenger.autotest.base;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.expect.ExpectKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.scenario.TestscenarioFraAutotestKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.scenario.TestscenarioKlient;
import no.nav.foreldrepenger.autotest.util.http.BasicHttpSession;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

import java.io.IOException;

public abstract class TestScenarioTestBase extends TestBase {

    protected TestscenarioKlient testscenarioKlient;
    protected TestscenarioFraAutotestKlient testscenarioFraAutotestKlient;
    protected ExpectKlient expectKlient;

    public TestScenarioTestBase() {
        testscenarioKlient = new TestscenarioKlient(BasicHttpSession.session());
        testscenarioFraAutotestKlient = new TestscenarioFraAutotestKlient(BasicHttpSession.session());
        expectKlient = new ExpectKlient(BasicHttpSession.session());
    }

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i VTP")
    @Deprecated
    protected TestscenarioDto opprettTestscenarioFraVTPTemplate(String id) throws IOException {
        return testscenarioKlient.opprettTestscenarioFraVTPTemplate(id);
    }

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i VTP")
    @Deprecated
    protected TestscenarioDto opprettScenarioMedPrivatArbeidsgiverFraVTPTemplate(String id, String aktorId) throws IOException {
        return testscenarioKlient.opprettTestscenarioMedAktorIdFraVTPTemplate(id, aktorId);
    }

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i Autotest")
    protected TestscenarioDto opprettTestscenario(String id) throws IOException {
        Object testscenarioObject = testscenarioRepositoryImpl.hentScenario(id);
        return (testscenarioObject == null ? null : testscenarioFraAutotestKlient.initialiserTestscenario(id, testscenarioObject));
    }
}
