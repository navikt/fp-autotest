package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;

public class Mor extends Søker {
    Mor(Fødselsnummer ident, BrukerRolle brukerRolle, Relasjoner relasjoner, InntektYtelseModell inntektYtelseModell) {
        super(ident, brukerRolle, relasjoner, inntektYtelseModell);
    }
}
