package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode = "5013")
public class VurderingAvForeldreansvarAndreLedd extends AksjonspunktBekreftelse {

    protected Boolean erVilkarOk;
    protected String avslagskode;

    public VurderingAvForeldreansvarAndreLedd() {
        super();
    }

    public void bekreftGodkjent() {
        erVilkarOk = true;
    }

    public void bekreftAvvist(String kode) {
        erVilkarOk = false;
        avslagskode = kode;
    }
}
