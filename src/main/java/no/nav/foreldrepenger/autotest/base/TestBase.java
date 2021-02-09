package no.nav.foreldrepenger.autotest.base;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.util.testscenario.TestscenarioHenter;

public abstract class TestBase {

    protected static final TestscenarioHenter TESTSCENARIO_HENTER = new TestscenarioHenter();

    // Logger for testruns
    protected Logger log;

    public TestBase() {
        log = LoggerFactory.getLogger(this.getClass());
    }

    /*
     * Verifisering
     */
    protected void verifiserLikhet(Object verdiGjeldende, Object verdiForventet) {
        verifiserLikhet(verdiGjeldende, verdiForventet, "Object");
    }

    @Step("Verifiserer likhet på {verdiNavn} mellom gjeldene {verdiGjeldende} og forventet {verdiForventet}")
    protected void verifiserLikhet(Object verdiGjeldende, Object verdiForventet, String verdiNavn) {
        verifiser(verdiGjeldende.equals(verdiForventet),
                String.format("%s har uventet verdi. forventet %s, var %s", verdiNavn, verdiForventet, verdiGjeldende));
    }

    @Step("Verifiserer at {listeGjeldende} inneholder {verdiForventet}")
    protected void verifiserInneholder(List<?> listeGjeldende, Object verdiForventet) {
        verifiser(listeGjeldende.stream().anyMatch(it -> it.equals(verdiForventet)), String
                .format("%s inneholder ikke forventet verdi. forventet å finne %s", listeGjeldende, verdiForventet));
    }

    protected void verifiser(boolean statement, String message) {
        if (!statement) {
            fail("Verifisering feilet: " + message);
        }
    }

}
