package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.time.LocalDate;

public class ApFaktaFeilutbetalingDetaljer {

    protected LocalDate fom;
    protected LocalDate tom;

    protected ApFaktaFeilutbetalingAarsak årsak = new ApFaktaFeilutbetalingAarsak();

    public ApFaktaFeilutbetalingDetaljer (LocalDate fom, LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }
}
