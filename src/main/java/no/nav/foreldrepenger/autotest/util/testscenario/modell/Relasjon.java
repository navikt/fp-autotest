package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;

public record Relasjon(Fødselsnummer relatertPersonsIdent, RelasjonType relatertPersonsRolle, LocalDate fødselsdato) {

}
