package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class KontrollerBesteberegningBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean besteberegningErKorrekt;

    public KontrollerBesteberegningBekreftelse godkjenn() {
        this.besteberegningErKorrekt = true;
        return this;
    }

    public Boolean getBesteberegningErKorrekt() {
        return besteberegningErKorrekt;
    }

    @Override
    public String aksjonspunktKode() {
        return "5062";
    }
}
