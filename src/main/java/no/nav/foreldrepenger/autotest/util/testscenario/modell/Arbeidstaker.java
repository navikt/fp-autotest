package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

record Arbeidstaker(Fødselsnummer fødselsnummer, AktørId aktørId, int månedsinntekt) {
}
