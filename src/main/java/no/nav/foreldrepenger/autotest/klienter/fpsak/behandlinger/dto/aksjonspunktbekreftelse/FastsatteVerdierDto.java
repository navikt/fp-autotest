package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FastsatteVerdierDto {

    protected Integer refusjon;
    protected Integer refusjonPrÅr;
    protected Integer fastsattBeløp;
    protected Integer fastsattÅrsbeløp;
    protected Kode inntektskategori;
    protected Boolean skalHaBesteberegning;

    public FastsatteVerdierDto(Integer fastsattBeløp) {
        this.fastsattBeløp = fastsattBeløp;
    }

    public FastsatteVerdierDto(Integer fastsattÅrsbeløp, Kode inntektskategori) {
        this.fastsattÅrsbeløp = fastsattÅrsbeløp;
        this.inntektskategori = inntektskategori;
    }

    public FastsatteVerdierDto(Integer fastsattÅrsbeløp, Integer refusjonPrÅr, Kode inntektskategori) {
        this.fastsattÅrsbeløp = fastsattÅrsbeløp;
        this.inntektskategori = inntektskategori;
        this.refusjonPrÅr = refusjonPrÅr;
    }

}
