package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public class BesteberegningFødendeKvinneAndelDto {

    protected int andelsnr;
    protected Boolean nyAndel;
    protected Boolean lagtTilAvSaksbehandler;
    protected FastsatteVerdierForBesteberegningDto fastsatteVerdier;

    public BesteberegningFødendeKvinneAndelDto() {
    }

    public BesteberegningFødendeKvinneAndelDto(double fastsattBeløp, String inntektskategori) {
        nyAndel = false;
        lagtTilAvSaksbehandler = false;
        fastsatteVerdier = new FastsatteVerdierForBesteberegningDto(fastsattBeløp, inntektskategori);
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
}
