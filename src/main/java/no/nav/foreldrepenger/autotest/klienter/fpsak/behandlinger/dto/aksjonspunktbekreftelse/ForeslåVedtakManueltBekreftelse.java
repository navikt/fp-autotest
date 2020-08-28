package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5028")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ForeslåVedtakManueltBekreftelse extends VedtaksbrevOverstyringDto {

    public ForeslåVedtakManueltBekreftelse() {
        super();
    }

    public ForeslåVedtakManueltBekreftelse(String begrunnelse, String overskrift, String fritekstBrev,
                                           Boolean skalBrukeOverstyrendeFritekstBrev) {
        super(begrunnelse, overskrift, fritekstBrev, skalBrukeOverstyrendeFritekstBrev);
    }
}
