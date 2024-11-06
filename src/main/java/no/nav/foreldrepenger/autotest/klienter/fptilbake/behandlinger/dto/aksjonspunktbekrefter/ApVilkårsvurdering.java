package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class ApVilkårsvurdering extends AksjonspunktBekreftelse {

    protected List<ApVilkårsvurderingDetaljer> vilkarsVurdertePerioder = new ArrayList<>();

    public ApVilkårsvurdering() {
        setBegrunnelse("Dette er en begrunnelse dannet av Autotest!");
    }

    public void addVilkårPeriode(LocalDate fom, LocalDate tom) {
        this.vilkarsVurdertePerioder.add(new ApVilkårsvurderingDetaljer(fom, tom));
    }

    public void addGeneriskVurdering() {
        for (ApVilkårsvurderingDetaljer apVilkårsvurderingDetaljer : vilkarsVurdertePerioder) {
            apVilkårsvurderingDetaljer.addGeneriskVurdering();
            apVilkårsvurderingDetaljer.vilkarResultatInfo.addGeneriskResultat();
            apVilkårsvurderingDetaljer.vilkarResultatInfo.aktsomhetInfo.addGeneriskAktsomhet();
        }
    }

    @Override
    public String aksjonspunktKode() {
        return "5002";
    }
}
