package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType;

public enum UttakKontoType {
    @JsonProperty("-")
    IKKE_SATT,
    FELLESPERIODE,
    MØDREKVOTE,
    FEDREKVOTE,
    FORELDREPENGER,
    FORELDREPENGER_FØR_FØDSEL;


    public KontoType tilKontoType() {
        return switch (this) {
            case FELLESPERIODE -> KontoType.FELLESPERIODE;
            case MØDREKVOTE -> KontoType.MØDREKVOTE;
            case FEDREKVOTE -> KontoType.FEDREKVOTE;
            case FORELDREPENGER -> KontoType.FORELDREPENGER;
            case FORELDREPENGER_FØR_FØDSEL -> KontoType.FORELDREPENGER_FØR_FØDSEL;
            case IKKE_SATT -> null;
        };
    }

    public static UttakKontoType tilUttakKontoType(KontoType stønadskontoType) {
        return switch (stønadskontoType) {
            case FELLESPERIODE -> UttakKontoType.FELLESPERIODE;
            case MØDREKVOTE -> UttakKontoType.MØDREKVOTE;
            case FEDREKVOTE -> UttakKontoType.FEDREKVOTE;
            case FORELDREPENGER -> UttakKontoType.FORELDREPENGER;
            case FORELDREPENGER_FØR_FØDSEL -> UttakKontoType.FORELDREPENGER_FØR_FØDSEL;
        };
    }

}
