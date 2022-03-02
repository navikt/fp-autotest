package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum KonsekvensForYtelsen {
    FORELDREPENGER_OPPHØRER,
    ENDRING_I_BEREGNING,
    ENDRING_I_UTTAK,
    ENDRING_I_FORDELING_AV_YTELSEN,
    INGEN_ENDRING,
    ENDRING_I_BEREGNING_OG_UTTAK,
    @JsonEnumDefaultValue
    UDEFINERT,
    ;

}
