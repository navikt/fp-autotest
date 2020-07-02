package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsatteVerdierDto {

    private Integer refusjon;
    private Integer refusjonPrÅr;
    private Integer fastsattBeløp;
    private Integer fastsattÅrsbeløp;
    private Kode inntektskategori;
    private Boolean skalHaBesteberegning;

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

    @JsonCreator
    public FastsatteVerdierDto(Integer refusjon, Integer refusjonPrÅr, Integer fastsattBeløp, Integer fastsattÅrsbeløp,
                               Kode inntektskategori, Boolean skalHaBesteberegning) {
        this.refusjon = refusjon;
        this.refusjonPrÅr = refusjonPrÅr;
        this.fastsattBeløp = fastsattBeløp;
        this.fastsattÅrsbeløp = fastsattÅrsbeløp;
        this.inntektskategori = inntektskategori;
        this.skalHaBesteberegning = skalHaBesteberegning;
    }

    public Integer getRefusjon() {
        return refusjon;
    }

    public Integer getRefusjonPrÅr() {
        return refusjonPrÅr;
    }

    public Integer getFastsattBeløp() {
        return fastsattBeløp;
    }

    public Integer getFastsattÅrsbeløp() {
        return fastsattÅrsbeløp;
    }

    public Kode getInntektskategori() {
        return inntektskategori;
    }

    public Boolean getSkalHaBesteberegning() {
        return skalHaBesteberegning;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsatteVerdierDto that = (FastsatteVerdierDto) o;
        return Objects.equals(refusjon, that.refusjon) &&
                Objects.equals(refusjonPrÅr, that.refusjonPrÅr) &&
                Objects.equals(fastsattBeløp, that.fastsattBeløp) &&
                Objects.equals(fastsattÅrsbeløp, that.fastsattÅrsbeløp) &&
                Objects.equals(inntektskategori, that.inntektskategori) &&
                Objects.equals(skalHaBesteberegning, that.skalHaBesteberegning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refusjon, refusjonPrÅr, fastsattBeløp, fastsattÅrsbeløp, inntektskategori, skalHaBesteberegning);
    }

    @Override
    public String toString() {
        return "FastsatteVerdierDto{" +
                "refusjon=" + refusjon +
                ", refusjonPrÅr=" + refusjonPrÅr +
                ", fastsattBeløp=" + fastsattBeløp +
                ", fastsattÅrsbeløp=" + fastsattÅrsbeløp +
                ", inntektskategori=" + inntektskategori +
                ", skalHaBesteberegning=" + skalHaBesteberegning +
                '}';
    }
}
