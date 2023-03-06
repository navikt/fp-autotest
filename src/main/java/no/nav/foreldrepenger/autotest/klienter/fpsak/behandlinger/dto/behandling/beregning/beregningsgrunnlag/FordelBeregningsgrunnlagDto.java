package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FordelBeregningsgrunnlagDto {

    protected List<FordelBeregningsgrunnlagPeriodeDto> fordelBeregningsgrunnlagPerioder = new ArrayList<>();

    public List<FordelBeregningsgrunnlagPeriodeDto> getFordelBeregningsgrunnlagPerioder() {
        return fordelBeregningsgrunnlagPerioder;
    }
}
