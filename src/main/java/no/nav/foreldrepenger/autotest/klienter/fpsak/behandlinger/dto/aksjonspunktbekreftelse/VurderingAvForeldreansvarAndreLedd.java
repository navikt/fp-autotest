package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode = "5013")
public class VurderingAvForeldreansvarAndreLedd extends AksjonspunktBekreftelse {

    protected Boolean erVilkarOk;
    protected String avslagskode;

    public VurderingAvForeldreansvarAndreLedd() {
        super();
    }

    public VurderingAvForeldreansvarAndreLedd bekreftGodkjent() {
        erVilkarOk = true;
        return this;
    }

    public VurderingAvForeldreansvarAndreLedd bekreftAvvist(String kode) {
        erVilkarOk = false;
        avslagskode = kode;
        return this;
    }
}
