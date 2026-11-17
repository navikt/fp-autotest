package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.FaktaOmBeregningTilfelle;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FaktaOmBeregningDto {

    protected List<FaktaOmBeregningTilfelle> faktaOmBeregningTilfeller;
    protected AvklarAktiviteterDto avklarAktiviteter;
    protected List<FaktaOmBeregningAndelDto> kortvarigeArbeidsforhold;

    public List<FaktaOmBeregningAndelDto> getKortvarigeArbeidsforhold() {
        return kortvarigeArbeidsforhold == null ? List.of() : kortvarigeArbeidsforhold;
    }

    public List<FaktaOmBeregningTilfelle> getFaktaOmBeregningTilfeller() {
        return faktaOmBeregningTilfeller;
    }

    public AvklarAktiviteterDto getAvklarAktiviteter() {
        return avklarAktiviteter;
    }

}
