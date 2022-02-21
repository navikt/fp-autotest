package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto.persondetaljer;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto.PersonDetaljer;


public record Person (Fødselsnummer fødselsnummer,
                      String fornavn,
                      String mellomnavn,
                      String etternavn,
                      Kjønn kjønn,
                      LocalDate fødselsdato) implements PersonDetaljer {

    @JsonCreator
    public Person {
        Objects.requireNonNull(fødselsnummer,"Fødselsnummer kan ikke være null");
        Objects.requireNonNull(fornavn,"Fornavn kan ikke være null");
        Objects.requireNonNull(fornavn,"Etternavn kan ikke være null");
    }

}
