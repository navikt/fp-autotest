package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class MerkOpptjeningUtlandDto extends AksjonspunktBekreftelse {

    private UtlandDokumentasjonStatus dokStatus;

    public UtlandDokumentasjonStatus getDokStatus() {
        return dokStatus;
    }


    public MerkOpptjeningUtlandDto setDokStatus(UtlandDokumentasjonStatus dokStatus) {
        this.dokStatus = dokStatus;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5068";
    }

    public enum UtlandDokumentasjonStatus {
        DOKUMENTASJON_ER_INNHENTET,
        DOKUMENTASJON_VIL_BLI_INNHENTET,
        DOKUMENTASJON_VIL_IKKE_BLI_INNHENTET
    }
}
