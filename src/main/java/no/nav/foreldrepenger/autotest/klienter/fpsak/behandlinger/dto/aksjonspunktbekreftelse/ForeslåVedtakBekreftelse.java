package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public class ForeslåVedtakBekreftelse extends AksjonspunktBekreftelse {

    private String overskrift;
    private String fritekstBrev;
    private Boolean skalBrukeOverstyrendeFritekstBrev;

    @Override
    public String aksjonspunktKode() {
        return "5015";
    }
}
