package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5059")
public class VurderRefusjonBeregningsgrunnlagBekreftelse extends AksjonspunktBekreftelse {

    private final List<VurderRefusjonAndelBeregningsgrunnlagDto> fastsatteAndeler = new ArrayList<>();

    public VurderRefusjonBeregningsgrunnlagBekreftelse() {
        super();
    }


    public List<VurderRefusjonAndelBeregningsgrunnlagDto> getFastsatteAndeler() {
        return fastsatteAndeler;
    }

    public VurderRefusjonBeregningsgrunnlagBekreftelse setFastsattRefusjonFomForAllePerioder(LocalDate fastsattRefusjonFom) {
        for (var vurderRefusjonAndelBeregningsgrunnlagDto:getFastsatteAndeler()) {
            vurderRefusjonAndelBeregningsgrunnlagDto.setFastsattRefusjonFom(fastsattRefusjonFom);
        }
        return this;
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        super.oppdaterMedDataFraBehandling(fagsak, behandling);
        for (Arbeidsforhold arbeidsforhold : behandling.getInntektArbeidYtelse().getArbeidsforhold()) {
            var vurderRefusjonAndelBeregningsgrunnlagDto = new VurderRefusjonAndelBeregningsgrunnlagDto(
                    arbeidsforhold.getArbeidsgiverReferanse(),
                    null,
                    arbeidsforhold.getArbeidsforholdId(),
                    null);
            fastsatteAndeler.add(vurderRefusjonAndelBeregningsgrunnlagDto);

        }

    }
}
