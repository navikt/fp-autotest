package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeid.Arbeidsforhold;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode = "5059")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderRefusjonBeregningsgrunnlagBekreftelse extends AksjonspunktBekreftelse {

    private List<VurderRefusjonAndelBeregningsgrunnlagDto> fastsatteAndeler = new ArrayList<>();

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
                    arbeidsforhold.getArbeidsgiverIdentifikator(),
                    null,
                    arbeidsforhold.getArbeidsforholdId(),
                    null);
            fastsatteAndeler.add(vurderRefusjonAndelBeregningsgrunnlagDto);

        }

    }
}
