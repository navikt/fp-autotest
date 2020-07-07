package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagPeriodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FastsettBeregningsgrunnlagPeriodeDto {

    protected List<FastsettBeregningsgrunnlagAndelDto> andeler;
    protected LocalDate fom;
    protected LocalDate tom;

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

}
