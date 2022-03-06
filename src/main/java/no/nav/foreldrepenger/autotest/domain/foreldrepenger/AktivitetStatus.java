package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AktivitetStatus {
    ARBEIDSAVKLARINGSPENGER("AAP"),
    ARBEIDSTAKER("AT"),
    DAGPENGER("DP"),
    FRILANSER("FL"),
    MILITÆR_ELLER_SIVIL("MS"),
    SELVSTENDIG_NÆRINGSDRIVENDE("SN"),
    KOMBINERT_AT_FL("AT_FL"),
    KOMBINERT_AT_SN("AT_SN"),
    KOMBINERT_FL_SN("FL_SN"),
    KOMBINERT_AT_FL_SN("AT_FL_SN"),
    BRUKERS_ANDEL("BA"),
    KUN_YTELSE("KUN_YTELSE"),
    TTLSTØTENDE_YTELSE("TY"),
    VENTELØNN_VARTPENGER("VENTELØNN_VARTPENGER"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private final String kode;

    AktivitetStatus(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }

}
