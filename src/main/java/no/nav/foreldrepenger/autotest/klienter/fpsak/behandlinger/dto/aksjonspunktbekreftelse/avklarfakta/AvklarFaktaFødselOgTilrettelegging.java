package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.generator.familie.ArbeidsforholdId;

@BekreftelseKode(kode = "5091")
public class AvklarFaktaFødselOgTilrettelegging extends AksjonspunktBekreftelse {

    protected LocalDate termindato;
    protected LocalDate fødselsdato;
    protected List<Arbeidsforhold> bekreftetSvpArbeidsforholdList;

    public AvklarFaktaFødselOgTilrettelegging() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        this.termindato = behandling.getTilrettelegging().getTermindato();
        this.fødselsdato = behandling.getTilrettelegging().getFødselsdato();
        this.bekreftetSvpArbeidsforholdList = behandling.getTilrettelegging().getArbeidsforholdList();
    }

    public List<Arbeidsforhold> getBekreftetSvpArbeidsforholdList() {
        return bekreftetSvpArbeidsforholdList;
    }

    public void setSkalBrukesTilFalseForArbeidsforhold(ArbeidsforholdId arbeidsforholdId) {
        setSkalBrukesTilFalseForAngitteArbeidsforhold(List.of(arbeidsforholdId));
    }

    public void setSkalBrukesTilFalseForAngitteArbeidsforhold(List<ArbeidsforholdId> arbeidsforholdId) {
        for (var arbeidsforhold : getBekreftetSvpArbeidsforholdList()) {
            if (arbeidsforholdId.contains(new ArbeidsforholdId(arbeidsforhold.getEksternArbeidsforholdReferanse()))) {
                arbeidsforhold.setSkalBrukes(false);
            }
        }
    }
}
