package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import com.fasterxml.jackson.annotation.JsonValue;

public record ArbeidsforholdId(@JsonValue String arbeidsforholdId) {

    @Override
    public String arbeidsforholdId() {
        return arbeidsforholdId;
    }
}
