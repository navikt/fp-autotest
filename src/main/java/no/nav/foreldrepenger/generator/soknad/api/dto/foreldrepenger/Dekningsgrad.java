package no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Dekningsgrad {
    ÅTTI("80"),
    HUNDRE("100");

    private final String verdi;

    Dekningsgrad(String verdi) {
        this.verdi = verdi;
    }

    @JsonValue
    public String verdi() {
        return verdi;
    }
}
