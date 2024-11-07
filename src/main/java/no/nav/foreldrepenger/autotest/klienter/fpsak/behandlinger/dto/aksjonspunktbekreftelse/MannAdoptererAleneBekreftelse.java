package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public class MannAdoptererAleneBekreftelse extends AksjonspunktBekreftelse {

    protected boolean mannAdoptererAlene;

    public MannAdoptererAleneBekreftelse bekreftMannAdoptererAlene() {
        mannAdoptererAlene = true;
        return this;
    }

    public MannAdoptererAleneBekreftelse bekreftMannAdoptererIkkeAlene() {
        mannAdoptererAlene = false;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5006";
    }
}
