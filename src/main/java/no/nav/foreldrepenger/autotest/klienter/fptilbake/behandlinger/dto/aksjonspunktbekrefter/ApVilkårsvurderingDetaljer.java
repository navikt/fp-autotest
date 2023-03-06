package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.time.LocalDate;

public class ApVilkårsvurderingDetaljer {

    protected String begrunnelse;
    protected LocalDate fom;
    protected LocalDate tom;
    protected Vilkår vilkarResultat;

    protected ApVilkårsvurderingResultatInfo vilkarResultatInfo = new ApVilkårsvurderingResultatInfo();

    public ApVilkårsvurderingDetaljer(LocalDate fom, LocalDate tom) {
        this.begrunnelse = "Dette er en vilkårsvurdering skrevet av Autotest!";
        this.fom = fom;
        this.tom = tom;
    }

    public void addGeneriskVurdering() {
        this.vilkarResultat = Vilkår.FORSTO_BURDE_FORSTAATT;
    }
}
