package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

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
    UDEFINERT("-"),
    ;

    @JsonValue
    private final String kode;

    Inntektskategori() {
        this(null);
    }

    Inntektskategori(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
