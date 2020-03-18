package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

import java.time.LocalDate;

@BekreftelseKode(kode="5001")
public class AvklarFaktaTerminBekreftelse extends AksjonspunktBekreftelse{

    protected int antallBarn;
    protected LocalDate utstedtdato;
    protected LocalDate termindato;

    public AvklarFaktaTerminBekreftelse() {
        super();
    }

    public void setAntallBarn(int antallBarn) {
        this.antallBarn = antallBarn;
    }
    public AvklarFaktaTerminBekreftelse antallBarn(int antallBarn) {
        setAntallBarn(antallBarn);
        return this;
    }

    public AvklarFaktaTerminBekreftelse setUtstedtdato(LocalDate utstedtdato) {
        this.utstedtdato = utstedtdato;
        return this;
    }

    public AvklarFaktaTerminBekreftelse utstedtdato(LocalDate utstedtdato) {
        setUtstedtdato(utstedtdato);
        return this;
    }

    public AvklarFaktaTerminBekreftelse setTermindato(LocalDate termindato) {
        this.termindato = termindato;
        return this;
    }

    public AvklarFaktaTerminBekreftelse termindato(LocalDate termindato) {
        setTermindato(termindato);
        return this;
    }


}
