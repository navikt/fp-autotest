package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;

public class Far extends Søker {
    Far(Fødselsnummer ident, AktørId aktørId, InntektYtelseModell inntektYtelseModell, Innsender innsenderType) {
        super(ident, aktørId, inntektYtelseModell, innsenderType);
    }
}
