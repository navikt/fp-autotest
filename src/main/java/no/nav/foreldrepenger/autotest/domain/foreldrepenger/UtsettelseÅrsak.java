package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum UtsettelseÅrsak {
    @JsonEnumDefaultValue
    @JsonProperty("-")
    UDEFINERT,
    ARBEID,
    FRI,
    HV_OVELSE,
    INSTITUSJONSOPPHOLD_BARNET,
    INSTITUSJONSOPPHOLD_SØKER,
    LOVBESTEMT_FERIE,
    NAV_TILTAK,
    SYKDOM,
}
