package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsatteVerdier {

    private Integer refusjon;
    private Integer fastsattBeløp;
    private Kode inntektskategori;

    public FastsatteVerdier(Integer refusjon, Integer fastsattBeløp, Kode inntektskategori) {
        this.refusjon = refusjon;
        this.fastsattBeløp = fastsattBeløp;
        this.inntektskategori = inntektskategori;
    }

    public Integer getRefusjon() {
        return refusjon;
    }

    public void setRefusjon(Integer refusjon) {
        this.refusjon = refusjon;
    }

    public Integer getFastsattBeløp() {
        return fastsattBeløp;
    }

    public void setFastsattBeløp(Integer fastsattBeløp) {
        this.fastsattBeløp = fastsattBeløp;
    }

    public Kode getInntektskategori() {
        return inntektskategori;
    }

    public void setInntektskategori(Kode inntektskategori) {
        this.inntektskategori = inntektskategori;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsatteVerdier that = (FastsatteVerdier) o;
        return Objects.equals(refusjon, that.refusjon) &&
                Objects.equals(fastsattBeløp, that.fastsattBeløp) &&
                Objects.equals(inntektskategori, that.inntektskategori);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refusjon, fastsattBeløp, inntektskategori);
    }

    @Override
    public String toString() {
        return "FastsatteVerdier{" +
                "refusjon=" + refusjon +
                ", fastsattBeløp=" + fastsattBeløp +
                ", inntektskategori=" + inntektskategori +
                '}';
    }
}
