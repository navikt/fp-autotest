package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;

public class Far extends Søker {
    Far(Fødselsnummer ident, InntektYtelseModell inntektYtelseModell) {
        super(ident, inntektYtelseModell);
    }
}
