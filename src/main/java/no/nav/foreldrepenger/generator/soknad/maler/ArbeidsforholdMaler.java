package no.nav.foreldrepenger.generator.soknad.maler;

import no.nav.foreldrepenger.kontrakter.fpsoknad.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Orgnummer;
import no.nav.foreldrepenger.kontrakter.fpsoknad.svangerskapspenger.ArbeidsforholdDto;

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
