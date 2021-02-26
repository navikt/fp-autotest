package no.nav.foreldrepenger.autotest.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.util.testscenario.TestscenarioHenter;

public abstract class TestBase {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected static final TestscenarioHenter TESTSCENARIO_HENTER = new TestscenarioHenter();

    /*
     * Verifisering
     */
    protected void verifiserLikhet(Object verdiGjeldende, Object verdiForventet) {
        verifiserLikhet(verdiGjeldende, verdiForventet, "Object");
    }

    protected void verifiserLikhet(Object verdiGjeldende, Object verdiForventet, String verdiNavn) {
        verifiserLikhet(verdiGjeldende, verdiForventet, verdiNavn, "");
    }

    @Step("Verifiserer likhet på {verdiNavn} mellom gjeldene {verdiGjeldende} og forventet {verdiForventet}")
    protected static void verifiserLikhet(Object verdiGjeldende, Object verdiForventet, String verdiNavn, String tilleggsinfo) {
        var stringBuilder = new StringBuilder(String.format("%s har uventet verdi", verdiNavn));
        if (!tilleggsinfo.isBlank()) {
            stringBuilder.append(". ");
            stringBuilder.append(tilleggsinfo);
        }
        assertThat(verdiGjeldende)
                .as(stringBuilder.toString())
                .isEqualTo(verdiForventet);
    }


    protected void verifiser(boolean statement, String message) {
        if (!statement) {
            fail("Verifisering feilet: " + message);
        }
    }

}
