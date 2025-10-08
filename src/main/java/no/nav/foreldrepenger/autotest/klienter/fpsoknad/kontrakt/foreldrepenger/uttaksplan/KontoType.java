package no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.foreldrepenger.uttaksplan;

public enum KontoType {
    FELLESPERIODE,
    MØDREKVOTE,
    FEDREKVOTE,
    FORELDREPENGER,
    FORELDREPENGER_FØR_FØDSEL;

    public String getKode() {
        return name();
    }
}
