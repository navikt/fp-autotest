package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FastsatteVerdierDto {

    protected Integer refusjonPrÅr;
    protected Integer fastsattÅrsbeløpInklNaturalytelse;
    protected Kode inntektskategori;

    public FastsatteVerdierDto(Integer fastsattÅrsbeløp, Kode inntektskategori) {
        this.fastsattÅrsbeløpInklNaturalytelse = fastsattÅrsbeløp;
        this.inntektskategori = inntektskategori;
    }

    public FastsatteVerdierDto(Integer fastsattÅrsbeløp, Integer refusjonPrÅr, Kode inntektskategori) {
        this.fastsattÅrsbeløpInklNaturalytelse = fastsattÅrsbeløp;
        this.inntektskategori = inntektskategori;
        this.refusjonPrÅr = refusjonPrÅr;
    }

}
