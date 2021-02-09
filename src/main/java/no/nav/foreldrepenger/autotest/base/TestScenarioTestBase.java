package no.nav.foreldrepenger.autotest.base;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioJerseyKlient;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public abstract class TestScenarioTestBase extends TestBase {

    private final TestscenarioJerseyKlient testscenarioKlient = new TestscenarioJerseyKlient();

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i Autotest")
    protected TestscenarioDto opprettTestscenarioMedPrivatArbeidsgiver(String id, String aktorId, String ident) {
        Object testscenarioObject = TESTSCENARIO_HENTER.hentScenario(id);
        return testscenarioKlient.opprettTestscenarioMedAktorId(id, testscenarioObject, aktorId, ident);
    }

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i Autotest")
    protected TestscenarioDto opprettTestscenario(String id) {
        Object testscenarioObject = TESTSCENARIO_HENTER.hentScenario(id);
        return testscenarioKlient.opprettTestscenario(id, testscenarioObject);
    }
}
