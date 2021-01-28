package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode = "5060")
public class AvklarFaktaAleneomsorgBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean aleneomsorg;

    public AvklarFaktaAleneomsorgBekreftelse() {
        super();
    }

    public AvklarFaktaAleneomsorgBekreftelse bekreftBrukerHarAleneomsorg() {
        this.aleneomsorg = true;
        return this;
    }

    public AvklarFaktaAleneomsorgBekreftelse bekreftBrukerHarIkkeAleneomsorg() {
        this.aleneomsorg = false;
        return this;
    }

}
