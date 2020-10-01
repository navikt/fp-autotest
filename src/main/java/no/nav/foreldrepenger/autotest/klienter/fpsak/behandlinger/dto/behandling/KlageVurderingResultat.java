package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class KlageVurderingResultat {
    private Kode klageVurdering;
    private String begrunnelse;
    private String fritekstTilBrev;
    private Kode klageMedholdArsak;
    private String klageAvvistArsakNavn;
    private Kode klageVurderingOmgjoer;
    private String klageVurdertAv;
    private Boolean godkjentAvMedunderskriver;

    public Kode getKlageVurdering() {
        return klageVurdering;
    }

    public void setKlageVurdering(Kode klageVurdering) {
        this.klageVurdering = klageVurdering;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public String getFritekstTilBrev() {
        return fritekstTilBrev;
    }

    public void setFritekstTilBrev(String fritekstTilBrev) {
        this.fritekstTilBrev = fritekstTilBrev;
    }

    public Kode getKlageMedholdArsak() {
        return klageMedholdArsak;
    }

    public void setKlageMedholdArsak(Kode klageMedholdArsak) {
        this.klageMedholdArsak = klageMedholdArsak;
    }

    public String getKlageAvvistArsakNavn() {
        return klageAvvistArsakNavn;
    }

    public void setKlageAvvistArsakNavn(String klageAvvistArsakNavn) {
        this.klageAvvistArsakNavn = klageAvvistArsakNavn;
    }

    public Kode getKlageVurderingOmgjoer() {
        return klageVurderingOmgjoer;
    }

    public void setKlageVurderingOmgjoer(Kode klageVurderingOmgjoer) {
        this.klageVurderingOmgjoer = klageVurderingOmgjoer;
    }

    public String getKlageVurdertAv() {
        return klageVurdertAv;
    }

    public void setKlageVurdertAv(String klageVurdertAv) {
        this.klageVurdertAv = klageVurdertAv;
    }

    public Boolean getGodkjentAvMedunderskriver() {
        return godkjentAvMedunderskriver;
    }

    public void setGodkjentAvMedunderskriver(Boolean godkjentAvMedunderskriver) {
        this.godkjentAvMedunderskriver = godkjentAvMedunderskriver;
    }
}
