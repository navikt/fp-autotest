package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum HistorikkAktør {
    BESLUTTER("BESL"),
    SAKSBEHANDLER("SBH"),
    SØKER("SOKER"),
    ARBEIDSGIVER("ARBEIDSGIVER"),
    VEDTAKSLØSNINGEN("VL"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private final String kode;

    HistorikkAktør(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
