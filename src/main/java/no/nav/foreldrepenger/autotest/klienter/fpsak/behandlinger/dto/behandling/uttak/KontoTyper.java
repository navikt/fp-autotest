package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum KontoTyper {
    FELLESPERIODE,
    MØDREKVOTE,
    FEDREKVOTE,
    FORELDREPENGER,
    FORELDREPENGER_FØR_FØDSEL,
    @JsonEnumDefaultValue
    UNDEFINED;
}
