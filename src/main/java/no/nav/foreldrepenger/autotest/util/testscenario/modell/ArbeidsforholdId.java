package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import com.fasterxml.jackson.annotation.JsonValue;

public record ArbeidsforholdId(@JsonValue String value) {

    @Override
    public String value() {
        return value;
    }
}
