package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;

public class Mor extends Søker {
    Mor(Fødselsnummer ident, Relasjoner relasjoner, InntektYtelseModell inntektYtelseModell) {
        super(ident, relasjoner, inntektYtelseModell);
    }
}
