package no.nav.foreldrepenger.autotest.søknad.modell;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class Fødselsnummer {

    @JsonValue
    private final String fnr;

    public Fødselsnummer(String fnr) {
        this.fnr = fnr;
    }

    @JsonCreator
    public static Fødselsnummer valueOf(@JsonProperty("fnr") String fnr) {
        return new Fødselsnummer(fnr);
    }
}
