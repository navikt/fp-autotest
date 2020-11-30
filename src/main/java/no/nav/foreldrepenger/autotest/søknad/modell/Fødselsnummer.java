package no.nav.foreldrepenger.autotest.søknad.modell;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Fødselsnummer {

    @JsonValue
    private final String fnr;

    @JsonCreator
    public Fødselsnummer(@JsonProperty("fnr") String fnr) {
        this.fnr = fnr;
    }

    public static Fødselsnummer valueOf(String fnr) {
        return new Fødselsnummer(fnr);
    }

}
