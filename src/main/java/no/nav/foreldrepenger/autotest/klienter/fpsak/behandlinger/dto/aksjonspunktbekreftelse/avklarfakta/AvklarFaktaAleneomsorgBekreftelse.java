package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class AvklarFaktaAleneomsorgBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean aleneomsorg;
    private Boolean annenforelderHarRett;
    private Boolean annenforelderMottarUføretrygd;

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

    @Override
    public String aksjonspunktKode() {
        return "5060";
    }
}
