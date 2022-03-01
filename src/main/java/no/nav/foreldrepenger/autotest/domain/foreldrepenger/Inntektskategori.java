package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

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
    @JsonEnumDefaultValue
    UDEFINERT,
    ;

}
