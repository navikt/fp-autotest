package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger.SvpTilrettelegging;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

public class BekreftSvangerskapspengerDto extends AksjonspunktBekreftelse {

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

    private BekreftTilrettelegging mapTilArbeidsforhold(SvpTilrettelegging a) {
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

    public void leggTilSplittedeTilrettelegginger(List<BekreftTilrettelegging> bekreftTilrettelegginger) {
        this.bekreftetSvpArbeidsforholdList = bekreftTilrettelegginger;
    }
}
