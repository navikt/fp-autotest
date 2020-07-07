package no.nav.foreldrepenger.autotest.base;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.util.konfigurasjon.MiljoKonfigurasjon;
import no.nav.foreldrepenger.autotest.util.testscenario.TestscenarioHenter;

public abstract class TestBase {

    // Logger for testruns
    protected Logger log;
    protected static TestscenarioHenter testscenarioHenter;

    public TestBase() {
        log = LoggerFactory.getLogger(this.getClass());
    }

    /*
     * Global setup
     */
    @BeforeAll
    protected static void setUpAll() {
        MiljoKonfigurasjon.initProperties();
        testscenarioHenter = new TestscenarioHenter();
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
