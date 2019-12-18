package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode="5017")
public class VurderSokersOpplysningspliktBekreftelse extends AksjonspunktBekreftelse{

    protected Boolean erVilkarOk;

    public VurderSokersOpplysningspliktBekreftelse() {
        super();
    }

    public void bekreftGodkjent() {
        erVilkarOk = true;
    }

    public void bekreftAvvist() {
        erVilkarOk = false;
    }
}
