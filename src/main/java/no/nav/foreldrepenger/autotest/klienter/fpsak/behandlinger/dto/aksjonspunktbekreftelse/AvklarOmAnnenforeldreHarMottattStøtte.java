package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode = "5032")
public class AvklarOmAnnenforeldreHarMottattStøtte extends AksjonspunktBekreftelse {

    protected Boolean erVilkarOk;
    protected String avslagskode;

    public AvklarOmAnnenforeldreHarMottattStøtte() {
        super();
    }

    public AvklarOmAnnenforeldreHarMottattStøtte bekreftGodkjent() {
        erVilkarOk = true;
        return this;
    }

    public AvklarOmAnnenforeldreHarMottattStøtte bekreftAvvist(String kode) {
        erVilkarOk = false;
        avslagskode = kode;
        return this;
    }

}
