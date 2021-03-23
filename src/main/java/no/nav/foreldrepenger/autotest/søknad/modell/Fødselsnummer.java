package no.nav.foreldrepenger.autotest.søknad.modell;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record Fødselsnummer(@JsonValue String fnr) {

    @Override
    public String fnr() {
        return fnr;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Fødselsnummer fraOrgnummer(String fnr) {
        return new Fødselsnummer(fnr);
    }

    @Override
    public String toString() {
        return fnr();
    }
}
