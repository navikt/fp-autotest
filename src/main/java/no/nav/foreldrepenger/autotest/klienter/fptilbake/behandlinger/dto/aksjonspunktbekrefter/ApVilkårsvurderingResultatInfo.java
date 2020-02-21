package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApVilkårsvurderingResultatInfo {

    @JsonProperty("@type")
    protected String type;
    protected Aktsomhet aktsomhet;
    protected String begrunnelse;

    protected ApVilkårsvurderingAktsomhetInfo aktsomhetInfo = new ApVilkårsvurderingAktsomhetInfo();

    public ApVilkårsvurderingResultatInfo() {
        this.begrunnelse = "Dette er en aktsomhetsvurdering skrevet av Autotest!";
    }

    public void addGeneriskResultat() {
        this.type = "annet";
        this.aktsomhet = Aktsomhet.SIMPEL_UAKTSOM;
    }
}
