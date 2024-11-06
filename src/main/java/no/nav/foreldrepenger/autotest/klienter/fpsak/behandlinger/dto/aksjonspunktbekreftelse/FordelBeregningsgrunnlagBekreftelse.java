package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FordelBeregningsgrunnlagBekreftelse extends AksjonspunktBekreftelse {

    protected List<FastsettBeregningsgrunnlagPeriodeDto> endretBeregningsgrunnlagPerioder;

    @Override
    public String aksjonspunktKode() {
        return "5046";
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        var beregningsgrunnlag = behandling.getBeregningsgrunnlag();
        endretBeregningsgrunnlagPerioder = beregningsgrunnlag.getFaktaOmFordeling().getFordelBeregningsgrunnlag()
                .getFordelBeregningsgrunnlagPerioder().stream()
                .filter(FordelBeregningsgrunnlagPeriodeDto::isHarPeriodeAarsakGraderingEllerRefusjon)
                .map(p -> {
                    var bgPeriode = beregningsgrunnlag.getBeregningsgrunnlagPeriode(p.getFom());
                    return new FastsettBeregningsgrunnlagPeriodeDto(p, bgPeriode);
                })
                .collect(Collectors.toList());
    }

    public FordelBeregningsgrunnlagBekreftelse settFastsattBeløpOgInntektskategori(LocalDate fom, int fastsattBeløp,
                                                                                   Inntektskategori inntektskategori,
                                                                                   int andelsnr) {
        FastsettBeregningsgrunnlagAndelDto andel = getFastsettBeregningsgrunnlagAndelDto(fom, andelsnr);
        andel.setFastsatteVerdier(new FastsatteVerdierDto(fastsattBeløp, inntektskategori));
        return this;
    }

    public FordelBeregningsgrunnlagBekreftelse settFastsattBeløpOgInntektskategoriMedRefusjon(LocalDate fom,
            int fastsattBeløp, int refusjonPrÅr, Inntektskategori inntektskategori, int andelsnr) {
        FastsettBeregningsgrunnlagAndelDto andel = getFastsettBeregningsgrunnlagAndelDto(fom, andelsnr);
        andel.setFastsatteVerdier(new FastsatteVerdierDto(fastsattBeløp, refusjonPrÅr, inntektskategori));
        return this;
    }

    private FastsettBeregningsgrunnlagAndelDto getFastsettBeregningsgrunnlagAndelDto(LocalDate fom, int andelsnr) {
        FastsettBeregningsgrunnlagPeriodeDto periode = endretBeregningsgrunnlagPerioder.stream()
                .filter(p -> p.fom.isEqual(fom))
                .findFirst().orElseThrow();
        return periode.andeler.stream()
                .filter(a -> a.getAndelsnr() == andelsnr)
                .findFirst().orElseThrow();
    }

}
