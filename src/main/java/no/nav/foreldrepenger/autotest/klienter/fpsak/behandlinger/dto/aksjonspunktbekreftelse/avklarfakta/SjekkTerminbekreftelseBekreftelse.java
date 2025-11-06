package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class SjekkTerminbekreftelseBekreftelse extends AksjonspunktBekreftelse {

    protected int antallBarn;
    protected LocalDate utstedtdato;
    protected LocalDate termindato;

    @Override
    public String aksjonspunktKode() {
        return "5001";
    }

    public SjekkTerminbekreftelseBekreftelse setUtstedtdato(LocalDate utstedtdato) {
        this.utstedtdato = utstedtdato;
        return this;
    }

    public SjekkTerminbekreftelseBekreftelse setTermindato(LocalDate termindato) {
        return setTermindato(termindato, 1);
    }

    public SjekkTerminbekreftelseBekreftelse setTermindato(LocalDate termindato, int antallBarn) {
        this.termindato = termindato;
        this.antallBarn = antallBarn;
        return this;
    }
}
