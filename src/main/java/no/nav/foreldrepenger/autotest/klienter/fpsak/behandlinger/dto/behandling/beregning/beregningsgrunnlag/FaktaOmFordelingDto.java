package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FaktaOmFordelingDto {

    protected FordelBeregningsgrunnlagDto fordelBeregningsgrunnlag;

    public FordelBeregningsgrunnlagDto getFordelBeregningsgrunnlag() {
        return fordelBeregningsgrunnlag;
    }
}
