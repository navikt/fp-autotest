package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

/**
 * Brukes i uttaksresultat
 */
public enum UttakUtsettelseÅrsak {

    ARBEID,
    FERIE,
    SYKDOM_SKADE,
    SØKER_INNLAGT,
    BARN_INNLAGT,
    HV_OVELSE,
    NAV_TILTAK,
    FRI,
    @JsonEnumDefaultValue
    UDEFINERT
    ;

}
