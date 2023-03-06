package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonValue;

public enum BehandlingStatus {

    AVSLUTTET("AVSLU"),
    FATTER_VEDTAK("FVED"),
    IVERKSETTER_VEDTAK("IVED"),
    OPPRETTET("OPPRE"),
    UTREDES("UTRED"),
    ;

    @JsonValue
    private final String kode;

    BehandlingStatus(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
