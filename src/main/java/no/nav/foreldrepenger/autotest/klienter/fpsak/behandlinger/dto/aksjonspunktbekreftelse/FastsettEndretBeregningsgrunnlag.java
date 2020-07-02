package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettEndretBeregningsgrunnlag {

    private List<FastsettEndretBeregningsgrunnlagPeriode> endretBeregningsgrunnlagPerioder = new ArrayList<>();

    FastsettEndretBeregningsgrunnlag() {
    }

    @JsonCreator
    public FastsettEndretBeregningsgrunnlag(@JsonProperty("endretBeregningsgrunnlagPerioder")
            List<FastsettEndretBeregningsgrunnlagPeriode> endretBeregningsgrunnlagPerioder) {
        this.endretBeregningsgrunnlagPerioder = endretBeregningsgrunnlagPerioder;
    }

    void leggTilAndelTilPeriode(BeregningsgrunnlagPeriodeDto periode, BeregningsgrunnlagPrStatusOgAndelDto andel,
            FastsatteVerdier fastsatteVerdier) {
        Optional<FastsettEndretBeregningsgrunnlagPeriode> eksisterendePeriode = endretBeregningsgrunnlagPerioder
                .stream()
                .filter(p -> p.getFom().isEqual(periode.getBeregningsgrunnlagPeriodeFom())).findFirst();
        if (eksisterendePeriode.isPresent()) {
            eksisterendePeriode.get().leggTilAndel(andel, fastsatteVerdier);
        }
        endretBeregningsgrunnlagPerioder.add(new FastsettEndretBeregningsgrunnlagPeriode(andel, fastsatteVerdier,
                periode.getBeregningsgrunnlagPeriodeFom(), periode.getBeregningsgrunnlagPeriodeTom()));
    }

    public List<FastsettEndretBeregningsgrunnlagPeriode> getEndretBeregningsgrunnlagPerioder() {
        return endretBeregningsgrunnlagPerioder;
    }

    public void setEndretBeregningsgrunnlagPerioder(
            List<FastsettEndretBeregningsgrunnlagPeriode> endretBeregningsgrunnlagPerioder) {
        this.endretBeregningsgrunnlagPerioder = endretBeregningsgrunnlagPerioder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettEndretBeregningsgrunnlag that = (FastsettEndretBeregningsgrunnlag) o;
        return Objects.equals(endretBeregningsgrunnlagPerioder, that.endretBeregningsgrunnlagPerioder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(endretBeregningsgrunnlagPerioder);
    }

    @Override
    public String toString() {
        return "FastsettEndretBeregningsgrunnlag{" +
                "endretBeregningsgrunnlagPerioder=" + endretBeregningsgrunnlagPerioder +
                '}';
    }
}
