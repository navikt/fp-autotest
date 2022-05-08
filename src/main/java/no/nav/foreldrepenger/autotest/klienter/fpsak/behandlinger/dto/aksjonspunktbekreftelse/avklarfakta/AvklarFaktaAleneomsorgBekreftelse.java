package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode = "5060")
public class AvklarFaktaAleneomsorgBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean aleneomsorg;
    private Boolean annenforelderHarRett;
    private Boolean annenforelderMottarUføretrygd;

    public AvklarFaktaAleneomsorgBekreftelse() {
        super();
    }

    public AvklarFaktaAleneomsorgBekreftelse bekreftBrukerHarAleneomsorg() {
        this.aleneomsorg = true;
        return this;
    }

    public AvklarFaktaAleneomsorgBekreftelse bekreftBrukerHarIkkeAleneomsorgAnnenpartIkkeRett() {
        this.aleneomsorg = false;
        this.annenforelderHarRett = false;
        this.annenforelderMottarUføretrygd = false;
        return this;
    }

    public AvklarFaktaAleneomsorgBekreftelse bekreftBrukerHarIkkeAleneomsorgAnnenpartRett() {
        this.aleneomsorg = false;
        this.annenforelderHarRett = true;
        return this;
    }

}
