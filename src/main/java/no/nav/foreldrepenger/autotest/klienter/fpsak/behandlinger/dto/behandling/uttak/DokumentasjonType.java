package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

public enum DokumentasjonType {

    UTEN_OMSORG("UTEN_OMSORG"),
    ALENEOMSORG("ALENEOMSORG"),
    ANNEN_FORELDER_HAR_RETT("ANNEN_FORELDER_HAR_RETT"),
    SYK_SØKER("SYK_SOKER"),
    INNLAGT_SØKER("INNLAGT_SOKER"),
    INNLAGT_BARN("INNLAGT_BARN"),
    UTEN_DOKUMENTASJON("UTEN_DOKUMENTASJON"),
    INSTITUSJONSOPPHOLD_ANNEN_FORELDRE("INSTITUSJONSOPPHOLD_ANNEN_FORELDRE"),
    SYKDOM_ANNEN_FORELDER("SYKDOM_ANNEN_FORELDER"),
    IKKE_RETT_ANNEN_FORELDER("IKKE_RETT_ANNEN_FORELDER"),
    ALENEOMSORG_OVERFØRING("ALENEOMSORG_OVERFØRING"),
    ;

    private static final String KODEVERK = "UTTAK_DOKUMENTASJON_TYPE";

    private String kode;

    DokumentasjonType(String kode) {
        this.kode = kode;
    }

    public Kode tilKode() {
        return new Kode(KODEVERK, kode);
    }
}
