package no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger.tilrettelegging.arbeidsforhold;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode()
public class SelvstendigNæringsdrivende extends Arbeidsforhold {
    @NotNull
    private final String risikoFaktorer;
    @NotNull
    private final String tilretteleggingstiltak;
}
