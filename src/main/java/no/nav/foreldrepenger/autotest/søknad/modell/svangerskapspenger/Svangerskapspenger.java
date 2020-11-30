package no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import no.nav.foreldrepenger.autotest.søknad.modell.Ytelse;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.medlemskap.Medlemsskap;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger.tilrettelegging.Tilrettelegging;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Svangerskapspenger extends Ytelse {

    @NotNull
    private LocalDate termindato;
    private LocalDate fødselsdato;
    @Valid
    private Medlemsskap medlemsskap;
    @Valid
    private Opptjening opptjening;
    @Valid
    private List<Tilrettelegging> tilrettelegging;

    @JsonCreator
    public Svangerskapspenger(@JsonProperty("termindato") LocalDate termindato,
            @JsonProperty("fødselsdato") LocalDate fødselsdato,
            @JsonProperty("medlemsskap") Medlemsskap medlemsskap,
            @JsonProperty("opptjening") Opptjening opptjening,
            @JsonProperty("tilrettelegging") List<Tilrettelegging> tilrettelegging) {
        this.termindato = termindato;
        this.fødselsdato = fødselsdato;
        this.medlemsskap = medlemsskap;
        this.opptjening = opptjening;
        this.tilrettelegging = tilrettelegging;
    }

}
