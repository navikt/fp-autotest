package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public class FastsatteVerdierForBesteberegningDto {

    protected double fastsattBeløp;
    protected String inntektskategori;

    public FastsatteVerdierForBesteberegningDto() {}

    public FastsatteVerdierForBesteberegningDto(double fastsattBeløp, String inntektskategori) {
        super();
        this.fastsattBeløp = fastsattBeløp;
        this.inntektskategori = inntektskategori;
    }
}
