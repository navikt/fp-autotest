package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;

public class FastsatteVerdierForBesteberegningDto {

    protected double fastsattBeløp;
    protected Inntektskategori inntektskategori;

    public FastsatteVerdierForBesteberegningDto() {
    }

    public FastsatteVerdierForBesteberegningDto(double fastsattBeløp, Inntektskategori inntektskategori) {
        super();
        this.fastsattBeløp = fastsattBeløp;
        this.inntektskategori = inntektskategori;
    }
}
