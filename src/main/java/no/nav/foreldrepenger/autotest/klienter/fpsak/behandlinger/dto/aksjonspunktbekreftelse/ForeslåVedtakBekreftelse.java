package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode = "5015")
public class ForeslåVedtakBekreftelse extends AksjonspunktBekreftelse {

    private String overskrift;
    private String fritekstBrev;
    private Boolean skalBrukeOverstyrendeFritekstBrev;

    public ForeslåVedtakBekreftelse() {
        super();
    }

}
