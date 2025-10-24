package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.RefusjonTilVurderingAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Orgnummer;

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
        for (RefusjonTilVurderingAndelDto andel : behandling.getBeregningsgrunnlag().getRefusjonTilVurdering().getAndeler()) {
            var vurderRefusjonAndelBeregningsgrunnlagDto = new VurderRefusjonAndelBeregningsgrunnlagDto(
                    new Orgnummer(andel.getArbeidsgiver().getArbeidsgiverOrgnr()),
                    null,
                    andel.getInternArbeidsforholdRef(),
                    null);
            fastsatteAndeler.add(vurderRefusjonAndelBeregningsgrunnlagDto);

        }

    }
}
