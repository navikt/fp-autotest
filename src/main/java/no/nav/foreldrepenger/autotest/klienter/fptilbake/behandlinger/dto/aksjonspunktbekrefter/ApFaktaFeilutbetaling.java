package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.Fagsystem;

@BekreftelseKode(kode = "7003", fagsystem = Fagsystem.FPTILBAKE)
public class ApFaktaFeilutbetaling extends AksjonspunktBekreftelse {

    protected final List<ApFaktaFeilutbetalingDetaljer> feilutbetalingFakta = new ArrayList<>();

    public ApFaktaFeilutbetaling() {
        setBegrunnelse("Dette er en begrunnelse dannet av Autotest!");
    }

    public void addFaktaPeriode(LocalDate fom, LocalDate tom) {
        this.feilutbetalingFakta.add(new ApFaktaFeilutbetalingDetaljer(fom, tom));
    }

    public void addGeneriskVurdering(String ytelseType) {
        for (ApFaktaFeilutbetalingDetaljer apFaktaFeilutbetalingDetaljer : feilutbetalingFakta) {
            apFaktaFeilutbetalingDetaljer.årsak.addGeneriskHendelser(ytelseType);
        }
    }
}
