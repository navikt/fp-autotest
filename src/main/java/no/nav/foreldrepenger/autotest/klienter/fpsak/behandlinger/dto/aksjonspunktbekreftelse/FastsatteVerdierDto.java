package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FastsatteVerdierDto {

    private Integer refusjonPrÅr;
    private final Integer fastsattÅrsbeløpInklNaturalytelse;
    private final Inntektskategori inntektskategori;

    public FastsatteVerdierDto(Integer fastsattÅrsbeløp, Inntektskategori inntektskategori) {
        this.fastsattÅrsbeløpInklNaturalytelse = fastsattÅrsbeløp;
        this.inntektskategori = inntektskategori;
    }

    public FastsatteVerdierDto(Integer fastsattÅrsbeløp, Integer refusjonPrÅr, Inntektskategori inntektskategori) {
        this.fastsattÅrsbeløpInklNaturalytelse = fastsattÅrsbeløp;
        this.inntektskategori = inntektskategori;
        this.refusjonPrÅr = refusjonPrÅr;
    }

    public Integer getRefusjonPrÅr() {
        return refusjonPrÅr;
    }

    public Integer getFastsattÅrsbeløpInklNaturalytelse() {
        return fastsattÅrsbeløpInklNaturalytelse;
    }

    public Inntektskategori getInntektskategori() {
        return inntektskategori;
    }
}
