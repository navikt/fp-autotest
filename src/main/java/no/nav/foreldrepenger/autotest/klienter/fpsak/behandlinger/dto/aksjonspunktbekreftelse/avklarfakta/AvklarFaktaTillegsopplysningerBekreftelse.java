package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode = "5009")
public class AvklarFaktaTillegsopplysningerBekreftelse extends AksjonspunktBekreftelse {

    public AvklarFaktaTillegsopplysningerBekreftelse() {
        super();
    }

    @Override
    public String toString() {
        return String.format("FatterVedtakBekreftelse: {kode:%s, begrunnelse%s}", kode, begrunnelse);
    }

}
