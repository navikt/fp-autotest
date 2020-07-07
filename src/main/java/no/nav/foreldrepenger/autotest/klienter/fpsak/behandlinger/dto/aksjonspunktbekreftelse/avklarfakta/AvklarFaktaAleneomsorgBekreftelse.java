package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode = "5060")
public class AvklarFaktaAleneomsorgBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean aleneomsorg;

    public AvklarFaktaAleneomsorgBekreftelse() {
        super();
    }

    public void bekreftBrukerHarAleneomsorg() {
        this.aleneomsorg = true;
    }

    public void bekreftBrukerHarIkkeAleneomsorg() {
        this.aleneomsorg = false;
    }

}
