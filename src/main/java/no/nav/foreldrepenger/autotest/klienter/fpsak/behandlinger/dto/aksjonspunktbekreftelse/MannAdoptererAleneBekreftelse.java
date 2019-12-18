package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode="5006")
public class MannAdoptererAleneBekreftelse extends AksjonspunktBekreftelse{

    protected boolean mannAdoptererAlene;

    public MannAdoptererAleneBekreftelse() {
        super();
    }

    public void bekreftMannAdoptererAlene() {
        mannAdoptererAlene = true;
    }

    public void bekreftMannAdoptererIkkeAlene() {
        mannAdoptererAlene = false;
    }
}
