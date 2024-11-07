package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.arbeidInntektsmelding.ArbeidsforholdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.common.domain.Orgnummer;

public class VurderRefusjonBeregningsgrunnlagBekreftelse extends AksjonspunktBekreftelse {

    private final List<VurderRefusjonAndelBeregningsgrunnlagDto> fastsatteAndeler = new ArrayList<>();

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
    public String aksjonspunktKode() {
        return "5059";
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        super.oppdaterMedDataFraBehandling(fagsak, behandling);
        for (ArbeidsforholdDto arbeidsforhold : behandling.getArbeidOgInntektsmeldingDto().arbeidsforhold()) {
            var vurderRefusjonAndelBeregningsgrunnlagDto = new VurderRefusjonAndelBeregningsgrunnlagDto(
                    new Orgnummer(arbeidsforhold.arbeidsgiverIdent()),
                    null,
                    arbeidsforhold.internArbeidsforholdId(),
                    null);
            fastsatteAndeler.add(vurderRefusjonAndelBeregningsgrunnlagDto);

        }

    }
}
