package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode = "5014")
public class VurderingAvForeldreansvarFjerdeLedd extends AksjonspunktBekreftelse {

    protected Boolean erVilkarOk;
    protected String avslagskode;

    public VurderingAvForeldreansvarFjerdeLedd() {
        super();
    }

    public VurderingAvForeldreansvarFjerdeLedd bekreftGodkjent() {
        erVilkarOk = true;
        return this;
    }

    public VurderingAvForeldreansvarFjerdeLedd bekreftAvvist(String kode) {
        erVilkarOk = false;
        avslagskode = kode;
        return this;
    }

}
