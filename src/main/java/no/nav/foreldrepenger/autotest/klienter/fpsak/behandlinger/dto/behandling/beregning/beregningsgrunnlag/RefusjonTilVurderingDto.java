package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RefusjonTilVurderingDto {
    private List<RefusjonTilVurderingAndelDto> andeler;

    public List<RefusjonTilVurderingAndelDto> getAndeler() {
        return andeler;
    }

    public void setAndeler(List<RefusjonTilVurderingAndelDto> andeler) {
        this.andeler = andeler;
    }
}
