package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.time.LocalDate;

public class ApFaktaFeilutbetalingDetaljer {

    protected final LocalDate fom;
    protected final LocalDate tom;

    protected final ApFaktaFeilutbetalingAarsak årsak = new ApFaktaFeilutbetalingAarsak();

    public ApFaktaFeilutbetalingDetaljer(LocalDate fom, LocalDate tom) {
        this.fom = fom;
        this.tom = tom;
    }
}
