package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode="5046")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FordelBeregningsgrunnlagBekreftelse extends AksjonspunktBekreftelse {

    protected List<FastsettBeregningsgrunnlagPeriodeDto> endretBeregningsgrunnlagPerioder;

    public FordelBeregningsgrunnlagBekreftelse() {
        super();
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        Beregningsgrunnlag beregningsgrunnlag = behandling.getBeregningsgrunnlag();
        endretBeregningsgrunnlagPerioder = beregningsgrunnlag.getFaktaOmFordeling().getFordelBeregningsgrunnlag()
                .getFordelBeregningsgrunnlagPerioder()
                .stream()
                .filter(FordelBeregningsgrunnlagPeriodeDto::isHarPeriodeAarsakGraderingEllerRefusjon)
                .map(p -> {
                    BeregningsgrunnlagPeriodeDto bgPeriode = beregningsgrunnlag.getBeregningsgrunnlagPeriode(p.getFom());
                    return new FastsettBeregningsgrunnlagPeriodeDto(p, bgPeriode);
                }).collect(Collectors.toList());
    }

    public FordelBeregningsgrunnlagBekreftelse settFastsattBeløpOgInntektskategori(LocalDate fom, int fastsattBeløp, Kode inntektskategori, int andelsnr){
        FastsettBeregningsgrunnlagPeriodeDto periode = endretBeregningsgrunnlagPerioder.stream()
                .filter(p -> p.fom.isEqual(fom))
                .findFirst().get();
        FastsettBeregningsgrunnlagAndelDto andel = periode.andeler.stream()
                .filter(a -> a.getAndelsnr() == andelsnr)
                .findFirst().get();
        andel.setFastsatteVerdier(new FastsatteVerdierDto(fastsattBeløp, inntektskategori));
        return this;
    }

    public FordelBeregningsgrunnlagBekreftelse settFastsattBeløpOgInntektskategoriMedRefusjon(LocalDate fom, int fastsattBeløp, int refusjonPrÅr, Kode inntektskategori, int andelsnr){
        FastsettBeregningsgrunnlagPeriodeDto periode = endretBeregningsgrunnlagPerioder.stream()
                .filter(p -> p.fom.isEqual(fom))
                .findFirst().get();
        FastsettBeregningsgrunnlagAndelDto andel = periode.andeler.stream()
                .filter(a -> a.getAndelsnr() == andelsnr)
                .findFirst().get();
        andel.setFastsatteVerdier(new FastsatteVerdierDto(fastsattBeløp, refusjonPrÅr, inntektskategori));
        return this;
    }


}
