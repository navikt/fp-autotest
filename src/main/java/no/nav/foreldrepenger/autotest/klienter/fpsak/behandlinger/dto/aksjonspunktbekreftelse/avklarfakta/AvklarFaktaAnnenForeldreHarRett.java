package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class AvklarFaktaAnnenForeldreHarRett extends AksjonspunktBekreftelse {

    protected boolean annenforelderHarRett;
    protected Boolean annenforelderMottarUføretrygd;
    protected Boolean annenForelderHarRettEØS;

    public AvklarFaktaAnnenForeldreHarRett setAnnenforelderHarRett(boolean annenforelderHarRett) {
        this.annenforelderHarRett = annenforelderHarRett;
        return this;
    }

    public AvklarFaktaAnnenForeldreHarRett setAnnenforelderMottarUføretrygd(Boolean annenforelderMottarUføretrygd) {
        this.annenforelderMottarUføretrygd = annenforelderMottarUføretrygd;
        return this;
    }

    public AvklarFaktaAnnenForeldreHarRett setAnnenForelderHarRettEØS(boolean annenForelderHarRettEØS) {
        this.annenForelderHarRettEØS = annenForelderHarRettEØS;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5086";
    }
}
