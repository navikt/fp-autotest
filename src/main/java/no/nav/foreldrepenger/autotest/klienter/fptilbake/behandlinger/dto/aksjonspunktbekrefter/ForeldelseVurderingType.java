package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ForeldelseVurderingType {

    IKKE_VURDERT("IKKE_VURDERT"),
    FORELDET("FORELDET"),
    IKKE_FORELDET("IKKE_FORELDET"),
    TILLEGGSFRIST("TILLEGGSFRIST");

    private final String kode;

    ForeldelseVurderingType(String kode) {
        this.kode = kode;
    }

    @JsonValue
    public String getKode() {
        return kode;
    }
}
