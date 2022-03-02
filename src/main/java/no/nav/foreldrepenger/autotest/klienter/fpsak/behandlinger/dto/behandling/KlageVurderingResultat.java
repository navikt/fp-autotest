package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class KlageVurderingResultat {
    private String klageVurdering;
    private String begrunnelse;
    private String fritekstTilBrev;
    private String klageMedholdArsak;
    private String klageAvvistArsakNavn;
    private String klageVurderingOmgjoer;
    private String klageVurdertAv;
    private Boolean godkjentAvMedunderskriver;

    public String getKlageVurdering() {
        return klageVurdering;
    }

    public void setKlageVurdering(String klageVurdering) {
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

    public String getKlageMedholdArsak() {
        return klageMedholdArsak;
    }

    public void setKlageMedholdArsak(String klageMedholdArsak) {
        this.klageMedholdArsak = klageMedholdArsak;
    }

    public String getKlageAvvistArsakNavn() {
        return klageAvvistArsakNavn;
    }

    public void setKlageAvvistArsakNavn(String klageAvvistArsakNavn) {
        this.klageAvvistArsakNavn = klageAvvistArsakNavn;
    }

    public String getKlageVurderingOmgjoer() {
        return klageVurderingOmgjoer;
    }

    public void setKlageVurderingOmgjoer(String klageVurderingOmgjoer) {
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
