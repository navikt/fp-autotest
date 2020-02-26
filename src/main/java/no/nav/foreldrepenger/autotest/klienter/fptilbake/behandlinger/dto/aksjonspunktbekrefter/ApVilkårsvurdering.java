package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import no.nav.foreldrepenger.autotest.klienter.Fagsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AksjonspunktKode(kode = "5002", fagsystem = Fagsystem.FPTILBAKE)
public class ApVilkårsvurdering extends AksjonspunktBehandling {

    protected List<ApVilkårsvurderingDetaljer> vilkarsVurdertePerioder = new ArrayList<>();

    public ApVilkårsvurdering () {
        this.kode = "5002";
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
}
