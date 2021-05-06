package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;

public class Far extends Søker {
    Far(Fødselsnummer ident, Fødselsnummer identAnnenpart, InntektYtelseModell inntektYtelseModell) {
        super(ident, identAnnenpart, inntektYtelseModell);
    }
}
