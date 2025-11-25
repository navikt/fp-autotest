package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Brukes i uttaksresultat
 */
public enum UttakresultatUtsettelseÅrsak {

    ARBEID("ARBEID"),
    FERIE("FERIE"),
    SYKDOM_SKADE("SYKDOM_SKADE"),
    SØKER_INNLAGT("SØKER_INNLAGT"),
    BARN_INNLAGT("BARN_INNLAGT"),
    HV_OVELSE("HV_OVELSE"),
    NAV_TILTAK("NAV_TILTAK"),
    FRI("FRI"),
    UDEFINERT("-"),
    ;

    @JsonValue
    private final String kode;

    UttakresultatUtsettelseÅrsak(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
