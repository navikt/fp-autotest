package no.nav.foreldrepenger.autotest.søknad.modell.felles;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({ "fom", "tom" })
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class LukketPeriode {

    @NotNull
    private final LocalDate fom;
    @NotNull
    private final LocalDate tom;

    @JsonCreator
    public LukketPeriode(@JsonProperty("fom") LocalDate fom, @JsonProperty("tom") LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }

}
