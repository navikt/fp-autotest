package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.generator.familie.ArbeidsforholdId;

import java.time.LocalDate;
import java.util.List;

public class AvklarFaktaFødselOgTilrettelegging extends AksjonspunktBekreftelse {

    protected LocalDate termindato;
    protected LocalDate fødselsdato;
    protected List<BekreftTilrettelegging> bekreftetSvpArbeidsforholdList;

    @Override
    public String aksjonspunktKode() {
        return "5091";
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        this.termindato = behandling.getTilrettelegging().getTermindato();
        this.fødselsdato = behandling.getTilrettelegging().getFødselsdato();
        this.bekreftetSvpArbeidsforholdList = behandling.getTilrettelegging().getArbeidsforholdList().stream().map(this::mapTilArbeidsforhold).toList();
    }

    private BekreftTilrettelegging mapTilArbeidsforhold(Arbeidsforhold a) {
        var tilrettelegging = new BekreftTilrettelegging();
        tilrettelegging.setArbeidsgiverReferanse(a.getArbeidsgiverReferanse());
        tilrettelegging.setTilretteleggingId(a.getTilretteleggingId());
        tilrettelegging.setSkalBrukes(a.getSkalBrukes());
        tilrettelegging.setTilretteleggingBehovFom(a.getTilretteleggingBehovFom());
        tilrettelegging.setTilretteleggingDatoer(a.getTilretteleggingDatoer());
        tilrettelegging.setAvklarteOppholdPerioder(a.getAvklarteOppholdPerioder());
        tilrettelegging.setInternArbeidsforholdReferanse(a.getInternArbeidsforholdReferanse());
        return tilrettelegging;
    }

    public List<BekreftTilrettelegging> getBekreftetSvpArbeidsforholdList() {
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

    public void setUtbetalingsgrad(ArbeidsforholdId arbeidsforholdId) {

    }

    public void setOverstyrtUtbetalingsgrad(ArbeidsforholdId arbeidsforholdId, BigDecimal overstyrtUtbetalingsgrad) {
        for (var arbeidsforhold : getBekreftetSvpArbeidsforholdList()) {
            if (arbeidsforholdId.equals(new ArbeidsforholdId(arbeidsforhold.getEksternArbeidsforholdReferanse()))) {
                arbeidsforhold.getTilretteleggingDatoer()
                    .forEach(dato -> dato.setOverstyrtUtbetalingsgrad(overstyrtUtbetalingsgrad));
            }
        }
    }
}
