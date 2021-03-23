package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record Orgnummer(@JsonValue String orgnummer) {

    @Override
    public String orgnummer() {
        return orgnummer;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Orgnummer fraOrgnummer(String orgnummer) {
        return new Orgnummer(orgnummer);
    }
}
