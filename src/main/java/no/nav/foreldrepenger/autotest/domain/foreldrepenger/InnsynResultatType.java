package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonValue;

public enum InnsynResultatType {

    INNVILGET("INNV"),
    DELVIS_INNVILGET("DELV"),
    AVVIST("AVVIST"),
    ;

    @JsonValue
    private final String kode;

    InnsynResultatType(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
