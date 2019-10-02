package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.FordelBeregningsgrunnlagPeriodeDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FastsettBeregningsgrunnlagPeriodeDto {

    protected List<FastsettBeregningsgrunnlagAndelDto> andeler;
    protected LocalDate fom;
    protected LocalDate tom;

    public FastsettBeregningsgrunnlagPeriodeDto(FordelBeregningsgrunnlagPeriodeDto periodeDto) {
        this.andeler = periodeDto.getFordelBeregningsgrunnlagAndeler().stream().map(FastsettBeregningsgrunnlagAndelDto::new).collect(Collectors.toList());
        this.fom = periodeDto.getFom();
        this.tom = periodeDto.getTom();
    }



}
