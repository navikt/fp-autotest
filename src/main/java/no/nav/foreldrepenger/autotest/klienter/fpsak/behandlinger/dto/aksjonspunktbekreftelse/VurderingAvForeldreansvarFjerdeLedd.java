package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode = "5014")
public class VurderingAvForeldreansvarFjerdeLedd extends AksjonspunktBekreftelse {

    protected Boolean erVilkarOk;
    protected String avslagskode;

    public VurderingAvForeldreansvarFjerdeLedd() {
        super();
        // TODO Auto-generated constructor stub
    }

    public void bekreftGodkjent() {
        erVilkarOk = true;
    }

    public void bekreftAvvist(String kode) {
        erVilkarOk = false;
        avslagskode = kode;
    }

}
