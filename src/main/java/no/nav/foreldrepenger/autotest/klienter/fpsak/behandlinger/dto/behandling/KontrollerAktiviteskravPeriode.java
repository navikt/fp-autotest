package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KontrollerAktiviteskravPeriode {

    private LocalDate fom;
    private LocalDate tom;
    private KontrollerAktivitetskravAvklaring avklaring;
    private String begrunnelse;

    public LocalDate getFom() {
        return fom;
    }

    public void setFom(LocalDate fom) {
        this.fom = fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public void setTom(LocalDate tom) {
        this.tom = tom;
    }

    public KontrollerAktivitetskravAvklaring getAvklaring() {
        return avklaring;
    }

    public void setAvklaring(KontrollerAktivitetskravAvklaring avklaring) {
        this.avklaring = avklaring;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    @Override
    public String toString() {
        return "KontrollerAktiviteskravPeriode{" + "fom=" + fom + ", tom=" + tom + ", avklaring=" + avklaring + ", begrunnelse='" + begrunnelse + '\'' + '}';
    }
}
