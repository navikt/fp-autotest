package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsatteVerdierDto {

    protected Integer refusjonPrÅr;
    protected Integer fastsattÅrsbeløpInklNaturalytelse;
    protected Kode inntektskategori;

    public FastsatteVerdierDto(Integer fastsattÅrsbeløp, Kode inntektskategori) {
        this.fastsattÅrsbeløpInklNaturalytelse = fastsattÅrsbeløp;
        this.inntektskategori = inntektskategori;
    }

    @JsonCreator
    public FastsatteVerdierDto(Integer fastsattÅrsbeløp, Integer refusjonPrÅr, Kode inntektskategori) {
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

    public Kode getInntektskategori() {
        return inntektskategori;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsatteVerdierDto that = (FastsatteVerdierDto) o;
        return Objects.equals(refusjonPrÅr, that.refusjonPrÅr) &&
                Objects.equals(fastsattÅrsbeløpInklNaturalytelse, that.fastsattÅrsbeløpInklNaturalytelse) &&
                Objects.equals(inntektskategori, that.inntektskategori);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refusjonPrÅr, fastsattÅrsbeløpInklNaturalytelse, inntektskategori);
    }

    @Override
    public String toString() {
        return "FastsatteVerdierDto{" +
                "refusjonPrÅr=" + refusjonPrÅr +
                ", fastsattÅrsbeløpInklNaturalytelse=" + fastsattÅrsbeløpInklNaturalytelse +
                ", inntektskategori=" + inntektskategori +
                '}';
    }
}
