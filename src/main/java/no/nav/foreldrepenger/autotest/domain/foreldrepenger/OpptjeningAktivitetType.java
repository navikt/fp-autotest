package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum OpptjeningAktivitetType {
    AAP,
    ARBEID,
    DAGPENGER,
    FORELDREPENGER,
    ETTERLØNN_SLUTTPAKKE,
    FRILANS,
    FRISINN,
    MILITÆR_ELLER_SIVILTJENESTE,
    NÆRING,
    OMSORGSPENGER,
    OPPLÆRINGSPENGER,
    PLEIEPENGER,
    SVANGERSKAPSPENGER,
    SYKEPENGER,
    VENTELØNN_VARTPENGER,
    VIDERE_ETTERUTDANNING,
    UTENLANDSK_ARBEIDSFORHOLD,
    UTDANNINGSPERMISJON,
    @JsonEnumDefaultValue
    UDEFINERT,
    ;

}
