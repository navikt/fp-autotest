package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

import java.time.LocalDate;
import java.util.List;

@BekreftelseKode(kode="5091")
public class AvklarFaktaFødselOgTilrettelegging extends AksjonspunktBekreftelse {
    protected LocalDate termindato;
    protected LocalDate fødselsdato;
    protected List<Arbeidsforhold> bekreftetSvpArbeidsforholdList;

    public AvklarFaktaFødselOgTilrettelegging(){
        super();
    }

    @Override
    public void setFagsakOgBehandling(Fagsak fagsak, Behandling behandling) {
        super.setFagsakOgBehandling(fagsak, behandling);
        this.termindato = behandling.getTilrettelegging().getTermindato();
        this.fødselsdato = behandling.getTilrettelegging().getFødselsdato();
        this.bekreftetSvpArbeidsforholdList = behandling.getTilrettelegging().getArbeidsforholdList();
    }
}
