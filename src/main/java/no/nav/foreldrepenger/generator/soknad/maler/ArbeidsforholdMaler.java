package no.nav.foreldrepenger.generator.soknad.maler;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.svangerskapspenger.ArbeidsforholdDto;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Orgnummer;

public final class ArbeidsforholdMaler {

    private ArbeidsforholdMaler() {
    }

    public static ArbeidsforholdDto virksomhet(Orgnummer orgnummer) {
        return new ArbeidsforholdDto.VirksomhetDto(orgnummer);
    }

    public static ArbeidsforholdDto privatArbeidsgiver(Fødselsnummer fnr) {
        return new ArbeidsforholdDto.PrivatArbeidsgiverDto(fnr);
    }

    public static ArbeidsforholdDto selvstendigNæringsdrivende() {
        return new ArbeidsforholdDto.SelvstendigNæringsdrivendeDto();
    }

}
