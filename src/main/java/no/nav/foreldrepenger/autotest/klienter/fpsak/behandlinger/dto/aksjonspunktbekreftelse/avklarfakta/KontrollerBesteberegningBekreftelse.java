package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode = "5062")
public class KontrollerBesteberegningBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean besteberegningErKorrekt;

    public KontrollerBesteberegningBekreftelse() {
        super();
    }

    public KontrollerBesteberegningBekreftelse godkjenn() {
        this.besteberegningErKorrekt = true;
        return this;
    }

    public Boolean getBesteberegningErKorrekt() {
        return besteberegningErKorrekt;
    }
}
