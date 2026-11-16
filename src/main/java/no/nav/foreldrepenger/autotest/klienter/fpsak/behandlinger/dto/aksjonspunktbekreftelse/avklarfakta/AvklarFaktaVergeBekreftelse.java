package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;

public class AvklarFaktaVergeBekreftelse extends AksjonspunktBekreftelse {

    protected String fnr;
    protected LocalDate gyldigFom;
    protected LocalDate gyldigTom;
    protected String navn;
    protected String vergeType;

    public AvklarFaktaVergeBekreftelse setVerge(Fødselsnummer fnr) {
        this.fnr = fnr.value();

        // Defaults
        gyldigFom = LocalDate.now().minusYears(1);
        gyldigTom = LocalDate.now().plusYears(2);
        navn = "Verge vergesen";
        vergeType = "BARN";
        return this;
    }

    public AvklarFaktaVergeBekreftelse setVergePeriodeStart(LocalDate fom) {
        gyldigFom = fom;
        return this;
    }

    public AvklarFaktaVergeBekreftelse setVergePeriodeSlutt(LocalDate tom) {
        gyldigTom = tom;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5030";
    }
}
