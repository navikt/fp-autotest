package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Inntektskategori {

    ARBEIDSTAKER,
    FRILANSER,
    SELVSTENDIG_NÆRINGSDRIVENDE,
    DAGPENGER,
    ARBEIDSAVKLARINGSPENGER,
    SJØMANN,
    DAGMAMMA,
    JORDBRUKER,
    FISKER,
    ARBEIDSTAKER_UTEN_FERIEPENGER,
    UDEFINERT,
    ;

    private final String kode;

    Inntektskategori() {
        this(null);
    }

    Inntektskategori(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    @JsonCreator
    public static Inntektskategori fraKode(String kode) {
        return Arrays.stream(Inntektskategori.values())
                .filter(value -> value.name().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet Inntektskategori " + kode));
    }

    public String getKode() {
        return kode;
    }
}
