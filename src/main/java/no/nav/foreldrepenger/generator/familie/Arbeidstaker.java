package no.nav.foreldrepenger.generator.familie;

import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;

record Arbeidstaker(Fødselsnummer fødselsnummer, AktørId aktørId, int månedsinntekt) {
}
