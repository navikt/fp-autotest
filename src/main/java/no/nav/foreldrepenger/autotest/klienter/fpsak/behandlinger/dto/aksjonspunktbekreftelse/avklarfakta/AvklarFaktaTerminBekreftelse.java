package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Soknad;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode="5001")
public class AvklarFaktaTerminBekreftelse extends AksjonspunktBekreftelse {

    protected int antallBarn;
    protected LocalDate utstedtdato;
    protected LocalDate termindato;

    public AvklarFaktaTerminBekreftelse() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        Soknad soknad = behandling.getSoknad();
        this.antallBarn = soknad.getAntallBarn();
        this.utstedtdato = soknad.getUtstedtdato();
        this.termindato = soknad.getTermindato();
    }

    public AvklarFaktaTerminBekreftelse setAntallBarn(int antallBarn) {
        this.antallBarn = antallBarn;
        return this;
    }

    public AvklarFaktaTerminBekreftelse setUtstedtdato(LocalDate utstedtdato) {
        this.utstedtdato = utstedtdato;
        return this;
    }

    public AvklarFaktaTerminBekreftelse setTermindato(LocalDate termindato) {
        this.termindato = termindato;
        return this;
    }
}
