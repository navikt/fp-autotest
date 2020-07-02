package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class DagpengeAndelLagtTilBesteberegningDto {

    private FastsatteVerdierForBesteberegningDto fastsatteVerdier;

    public DagpengeAndelLagtTilBesteberegningDto(@JsonProperty("fastsatteVerdier") FastsatteVerdierForBesteberegningDto fastsatteVerdier) {
        this.fastsatteVerdier = fastsatteVerdier;
    }

    public FastsatteVerdierForBesteberegningDto getFastsatteVerdier() {
        return fastsatteVerdier;
    }

    public void setFastsatteVerdier(FastsatteVerdierForBesteberegningDto fastsatteVerdier) {
        this.fastsatteVerdier = fastsatteVerdier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DagpengeAndelLagtTilBesteberegningDto that = (DagpengeAndelLagtTilBesteberegningDto) o;
        return Objects.equals(fastsatteVerdier, that.fastsatteVerdier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fastsatteVerdier);
    }

    @Override
    public String toString() {
        return "DagpengeAndelLagtTilBesteberegningDto{" +
                "fastsatteVerdier=" + fastsatteVerdier +
                '}';
    }
}
