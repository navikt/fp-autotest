package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode = "5086")
public class AvklarFaktaAnnenForeldreHarRett extends AksjonspunktBekreftelse {

    protected boolean annenforelderHarRett;

    public AvklarFaktaAnnenForeldreHarRett() {
        super();
    }

    public void setAnnenforelderHarRett(boolean annenforelderHarRett) {
        this.annenforelderHarRett = annenforelderHarRett;
    }

}
