package no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;

public enum BrukerIdType {
    @JsonEnumDefaultValue
    UKJENT,
    AKTOERID,
    FNR,
    ORGNR
}
