package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BesteberegningFødendeKvinneAndelDto {

    private int andelsnr;
    private Boolean nyAndel;
    private Boolean lagtTilAvSaksbehandler;
    private FastsatteVerdierForBesteberegningDto fastsatteVerdier;

    public BesteberegningFødendeKvinneAndelDto() {
    }

    @JsonCreator
    public BesteberegningFødendeKvinneAndelDto(double fastsattBeløp, String inntektskategori) {
        nyAndel = false;
        lagtTilAvSaksbehandler = false;
        fastsatteVerdier = new FastsatteVerdierForBesteberegningDto(fastsattBeløp, inntektskategori);
    }

    public int getAndelsnr() {
        return andelsnr;
    }

    public Boolean getNyAndel() {
        return nyAndel;
    }

    public Boolean getLagtTilAvSaksbehandler() {
        return lagtTilAvSaksbehandler;
    }

    public FastsatteVerdierForBesteberegningDto getFastsatteVerdier() {
        return fastsatteVerdier;
    }

    public void setAndelsnr(int andelsnr) {
        this.andelsnr = andelsnr;
    }

    public void setNyAndel(Boolean nyAndel) {
        this.nyAndel = nyAndel;
    }

    public void setLagtTilAvSaksbehandler(Boolean lagtTilAvSaksbehandler) {
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
    }

    public void setFastsatteVerdier(FastsatteVerdierForBesteberegningDto fastsatteVerdier) {
        this.fastsatteVerdier = fastsatteVerdier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BesteberegningFødendeKvinneAndelDto that = (BesteberegningFødendeKvinneAndelDto) o;
        return andelsnr == that.andelsnr &&
                Objects.equals(nyAndel, that.nyAndel) &&
                Objects.equals(lagtTilAvSaksbehandler, that.lagtTilAvSaksbehandler) &&
                Objects.equals(fastsatteVerdier, that.fastsatteVerdier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andelsnr, nyAndel, lagtTilAvSaksbehandler, fastsatteVerdier);
    }

    @Override
    public String toString() {
        return "BesteberegningFødendeKvinneAndelDto{" +
                "andelsnr=" + andelsnr +
                ", nyAndel=" + nyAndel +
                ", lagtTilAvSaksbehandler=" + lagtTilAvSaksbehandler +
                ", fastsatteVerdier=" + fastsatteVerdier +
                '}';
    }
}
