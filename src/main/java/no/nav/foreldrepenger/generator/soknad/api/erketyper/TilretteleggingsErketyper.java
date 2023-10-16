package no.nav.foreldrepenger.generator.soknad.api.erketyper;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.generator.soknad.api.dto.svangerskapspenger.ArbeidsforholdDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.svangerskapspenger.TilretteleggingDto;

public final class TilretteleggingsErketyper {

    private TilretteleggingsErketyper() {
    }

    public static TilretteleggingDto helTilrettelegging(LocalDate behovForTilretteleggingFom,
                                                        LocalDate tilrettelagtArbeidFom,
                                                        ArbeidsforholdDto arbeidsforhold) {
        return new TilretteleggingDto(
                TilretteleggingDto.Type.HEL,
                arbeidsforhold,
                null,
                behovForTilretteleggingFom,
                tilrettelagtArbeidFom,
                null, List.of()
        );
    }

    public static TilretteleggingDto delvisTilrettelegging(LocalDate behovForTilretteleggingFom,
                                                           LocalDate tilrettelagtArbeidFom,
                                                           ArbeidsforholdDto arbeidsforhold,
                                                           Number stillingsprosent) {
        return new TilretteleggingDto(
                TilretteleggingDto.Type.DELVIS,
                arbeidsforhold,
                stillingsprosent.doubleValue(),
                behovForTilretteleggingFom,
                tilrettelagtArbeidFom,
                null,
                List.of()
        );
    }

    public static TilretteleggingDto ingenTilrettelegging(LocalDate behovForTilretteleggingFom,
                                                            LocalDate slutteArbeidFom,
                                                            ArbeidsforholdDto arbeidsforhold) {
        return new TilretteleggingDto(
                TilretteleggingDto.Type.INGEN,
                arbeidsforhold,
                null,
                behovForTilretteleggingFom,
                null,
                slutteArbeidFom,
                List.of()
        );
    }

}
