package no.nav.foreldrepenger.generator.soknad.api.erketyper;

import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.generator.soknad.api.dto.svangerskapspenger.ArbeidsforholdDto;

public final class ArbeidsforholdErketyper {

    private ArbeidsforholdErketyper() {
    }

    public static ArbeidsforholdDto virksomhet(Orgnummer orgnummer) {
        return new ArbeidsforholdDto(
                ArbeidsforholdDto.Type.VIRKSOMHET,
                orgnummer.value(),
                null,
                null
                );
    }

    public static ArbeidsforholdDto privatArbeidsgiver(String fnr) {
        return new ArbeidsforholdDto(
                ArbeidsforholdDto.Type.PRIVAT,
                fnr,
                null,
                null
        );
    }

    public static ArbeidsforholdDto selvstendigNæringsdrivende() {
        return new ArbeidsforholdDto(
                ArbeidsforholdDto.Type.SELVSTENDIG,
                null,
                "risikofaktorer",
                "tilretteleggingstiltak"
        );
    }

}
