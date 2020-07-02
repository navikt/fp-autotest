package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagPeriodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettBeregningsgrunnlagPeriodeDto {

    private List<FastsettBeregningsgrunnlagAndelDto> andeler;
    private LocalDate fom;
    private LocalDate tom;

    @JsonCreator
    public FastsettBeregningsgrunnlagPeriodeDto(List<FastsettBeregningsgrunnlagAndelDto> andeler, LocalDate fom, LocalDate tom) {
        this.andeler = andeler;
        this.fom = fom;
        this.tom = tom;
    }

    public FastsettBeregningsgrunnlagPeriodeDto(FordelBeregningsgrunnlagPeriodeDto periodeDto,
                                                BeregningsgrunnlagPeriodeDto bgPeriodeDto) {

        this.andeler = periodeDto.getFordelBeregningsgrunnlagAndeler().stream().map(a -> {
            BeregningsgrunnlagPrStatusOgAndelDto bgAndel = bgPeriodeDto.getBeregningsgrunnlagPrStatusOgAndel().stream()
                    .filter(bga -> bga.getAndelsnr() == a.getAndelsnr()).findFirst().orElseThrow();
            return new FastsettBeregningsgrunnlagAndelDto(a, bgAndel);
        }).collect(Collectors.toList());
        this.fom = periodeDto.getFom();
        this.tom = periodeDto.getTom();
    }

    public List<FastsettBeregningsgrunnlagAndelDto> getAndeler() {
        return andeler;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettBeregningsgrunnlagPeriodeDto that = (FastsettBeregningsgrunnlagPeriodeDto) o;
        return Objects.equals(andeler, that.andeler) &&
                Objects.equals(fom, that.fom) &&
                Objects.equals(tom, that.tom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andeler, fom, tom);
    }

    @Override
    public String toString() {
        return "FastsettBeregningsgrunnlagPeriodeDto{" +
                "andeler=" + andeler +
                ", fom=" + fom +
                ", tom=" + tom +
                '}';
    }
}
