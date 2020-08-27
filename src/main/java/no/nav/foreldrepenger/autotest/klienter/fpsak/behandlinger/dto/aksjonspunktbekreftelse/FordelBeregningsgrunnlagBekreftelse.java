package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode = "5046")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FordelBeregningsgrunnlagBekreftelse extends AksjonspunktBekreftelse {

    private List<FastsettBeregningsgrunnlagPeriodeDto> endretBeregningsgrunnlagPerioder;

    public FordelBeregningsgrunnlagBekreftelse() {
        super();
    }

    public List<FastsettBeregningsgrunnlagPeriodeDto> getEndretBeregningsgrunnlagPerioder() {
        return endretBeregningsgrunnlagPerioder;
    }

    public void setEndretBeregningsgrunnlagPerioder(List<FastsettBeregningsgrunnlagPeriodeDto> endretBeregningsgrunnlagPerioder) {
        this.endretBeregningsgrunnlagPerioder = endretBeregningsgrunnlagPerioder;
    }

    public FordelBeregningsgrunnlagBekreftelse settFastsattBeløpOgInntektskategori(LocalDate fom, int fastsattBeløp,
            Kode inntektskategori, int andelsnr) {
        FastsettBeregningsgrunnlagPeriodeDto periode = endretBeregningsgrunnlagPerioder.stream()
                .filter(p -> p.getFom().isEqual(fom))
                .findFirst().get();
        FastsettBeregningsgrunnlagAndelDto andel = periode.getAndeler().stream()
                .filter(a -> a.getAndelsnr() == andelsnr)
                .findFirst().get();
        andel.setFastsatteVerdier(new FastsatteVerdierDto(fastsattBeløp, inntektskategori));
        return this;
    }

    public FordelBeregningsgrunnlagBekreftelse settFastsattBeløpOgInntektskategoriMedRefusjon(LocalDate fom,
            int fastsattBeløp, int refusjonPrÅr, Kode inntektskategori, int andelsnr) {
        FastsettBeregningsgrunnlagPeriodeDto periode = endretBeregningsgrunnlagPerioder.stream()
                .filter(p -> p.getFom().isEqual(fom))
                .findFirst().get();
        FastsettBeregningsgrunnlagAndelDto andel = periode.getAndeler().stream()
                .filter(a -> a.getAndelsnr() == andelsnr)
                .findFirst().get();
        andel.setFastsatteVerdier(new FastsatteVerdierDto(fastsattBeløp, refusjonPrÅr, inntektskategori));
        return this;
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        Beregningsgrunnlag beregningsgrunnlag = behandling.getBeregningsgrunnlag();
        endretBeregningsgrunnlagPerioder = beregningsgrunnlag.getFaktaOmFordeling().getFordelBeregningsgrunnlag()
                .getFordelBeregningsgrunnlagPerioder()
                .stream()
                .filter(FordelBeregningsgrunnlagPeriodeDto::isHarPeriodeAarsakGraderingEllerRefusjon)
                .map(p -> {
                    BeregningsgrunnlagPeriodeDto bgPeriode = beregningsgrunnlag
                            .getBeregningsgrunnlagPeriode(p.getFom());
                    return new FastsettBeregningsgrunnlagPeriodeDto(p, bgPeriode);
                }).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FordelBeregningsgrunnlagBekreftelse that = (FordelBeregningsgrunnlagBekreftelse) o;
        return Objects.equals(endretBeregningsgrunnlagPerioder, that.endretBeregningsgrunnlagPerioder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endretBeregningsgrunnlagPerioder);
    }

    @Override
    public String toString() {
        return "FordelBeregningsgrunnlagBekreftelse{" +
                "endretBeregningsgrunnlagPerioder=" + endretBeregningsgrunnlagPerioder +
                '}';
    }
}
