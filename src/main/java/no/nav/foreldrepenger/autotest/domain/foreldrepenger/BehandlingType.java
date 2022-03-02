package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BehandlingType {

    FØRSTEGANGSSØKNAD("BT-002"),
    KLAGE("BT-003"),
    REVURDERING("BT-004"),
    ANKE("BT-008"),
    INNSYN("BT-006"),
    ;

    @JsonValue
    private final String kode;

    BehandlingType(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
