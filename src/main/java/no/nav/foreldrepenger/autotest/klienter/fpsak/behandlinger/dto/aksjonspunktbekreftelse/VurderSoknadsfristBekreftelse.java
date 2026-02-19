package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public class VurderSoknadsfristBekreftelse extends AksjonspunktBekreftelse {

    protected boolean erVilkårOk;

    public VurderSoknadsfristBekreftelse() {
        super();
    }

    public VurderSoknadsfristBekreftelse bekreftVilkårErOk() {
        erVilkårOk = true;
        return this;
    }

    public VurderSoknadsfristBekreftelse bekreftVilkårErIkkeOk() {
        erVilkårOk = false;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5007";
    }

}
