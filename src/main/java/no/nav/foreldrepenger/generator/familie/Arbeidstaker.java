package no.nav.foreldrepenger.generator.familie;

import no.nav.foreldrepenger.kontrakter.fpsoknad.Fødselsnummer;

record Arbeidstaker(Fødselsnummer fødselsnummer, AktørId aktørId, int månedsinntekt) {
}
