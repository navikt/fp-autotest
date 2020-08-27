package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsatteVerdierForBesteberegningDto {

    private double fastsattBeløp;
    private String inntektskategori;

    public FastsatteVerdierForBesteberegningDto() {
    }

    @JsonCreator
    public FastsatteVerdierForBesteberegningDto(double fastsattBeløp, String inntektskategori) {
        this.fastsattBeløp = fastsattBeløp;
        this.inntektskategori = inntektskategori;
    }

    public double getFastsattBeløp() {
        return fastsattBeløp;
    }

    public String getInntektskategori() {
        return inntektskategori;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsatteVerdierForBesteberegningDto that = (FastsatteVerdierForBesteberegningDto) o;
        return Double.compare(that.fastsattBeløp, fastsattBeløp) == 0 &&
                Objects.equals(inntektskategori, that.inntektskategori);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fastsattBeløp, inntektskategori);
    }

    @Override
    public String toString() {
        return "FastsatteVerdierForBesteberegningDto{" +
                "fastsattBeløp=" + fastsattBeløp +
                ", inntektskategori='" + inntektskategori + '\'' +
                '}';
    }
}
