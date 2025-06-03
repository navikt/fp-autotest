package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;


public enum OpptjeningAktivitetType {
    AAP,
    ARBEID,
    ARBEID_UNDER_AAP,
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
    UDEFINERT("-"),
    ;

    @JsonValue
    private final String kode;

    OpptjeningAktivitetType() {
        this(null);
    }

    OpptjeningAktivitetType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
