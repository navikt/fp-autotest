package no.nav.foreldrepenger.autotest.base;


import static no.nav.foreldrepenger.autotest.util.junit.TestscenarioExtension.testscenarioHenter;
import static no.nav.foreldrepenger.autotest.util.junit.TestscenarioExtension.testscenarioKlient;

import org.junit.jupiter.api.extension.ExtendWith;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.util.junit.TestscenarioExtension;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

@ExtendWith({TestscenarioExtension.class})
public abstract class TestScenarioTestBase extends TestBase {

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i Autotest")
    protected TestscenarioDto opprettTestscenarioMedPrivatArbeidsgiver(String id, String aktorId, String ident) {
        Object testscenarioObject = testscenarioHenter.hentScenario(id);
        return testscenarioKlient.opprettTestscenarioMedAktorId(id, testscenarioObject, aktorId, ident);
    }

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i Autotest")
    protected TestscenarioDto opprettTestscenario(String id) {
        Object testscenarioObject = testscenarioHenter.hentScenario(id);
        return testscenarioKlient.opprettTestscenario(id, testscenarioObject);
    }
}
