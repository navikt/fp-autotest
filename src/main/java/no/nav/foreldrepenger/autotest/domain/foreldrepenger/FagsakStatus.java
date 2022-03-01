package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FagsakStatus {

    OPPRETTET("OPPR"),
    UNDER_BEHANDLING("UBEH"),
    LØPENDE("LOP"),
    AVSLUTTET("AVSLU"),
    ;
    @JsonValue
    private final String kode;

    FagsakStatus(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}


