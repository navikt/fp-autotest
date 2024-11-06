package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public class VurderingAvForeldreansvarAndreLedd extends AksjonspunktBekreftelse {

    protected Boolean erVilkarOk;
    protected String avslagskode;

    public void bekreftGodkjent() {
        erVilkarOk = true;
    }

    public void bekreftAvvist(String kode) {
        erVilkarOk = false;
        avslagskode = kode;
    }

    @Override
    public String aksjonspunktKode() {
        return "5013";
    }
}
