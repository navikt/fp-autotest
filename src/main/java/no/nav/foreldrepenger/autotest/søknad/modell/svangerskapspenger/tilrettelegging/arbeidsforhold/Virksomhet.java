package no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger.tilrettelegging.arbeidsforhold;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class Virksomhet extends Arbeidsforhold {

    @NotNull
    public final String orgnr;

    @JsonCreator
    public Virksomhet(@JsonProperty("orgnr") String orgnr) {
        this.orgnr = orgnr;
    }

}
