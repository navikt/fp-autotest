package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UttakResultatPeriode implements Serializable {

    private LocalDate fom;
    private LocalDate tom;
    private List<UttakResultatPeriodeAktivitet> aktiviteter;
    private Kode periodeResultatType;
    private String begrunnelse;
    private Kode periodeResultatÅrsak;
    private Kode manuellBehandlingÅrsak;
    private Kode graderingAvslagÅrsak;
    private Boolean flerbarnsdager;
    private Boolean samtidigUttak;
    private BigDecimal samtidigUttaksprosent;
    private Boolean graderingInnvilget;
    private Kode periodeType;
    private UttakUtsettelseÅrsak utsettelseType;
    private OppholdÅrsak oppholdÅrsak;
    private UttakResultatPeriodeAktivitet gradertAktivitet;

    public UttakResultatPeriode(LocalDate fom, LocalDate tom, List<UttakResultatPeriodeAktivitet> aktiviteter,
                                Kode periodeResultatType, String begrunnelse, Kode periodeResultatÅrsak,
                                Kode manuellBehandlingÅrsak, Kode graderingAvslagÅrsak, Boolean flerbarnsdager,
                                Boolean samtidigUttak, BigDecimal samtidigUttaksprosent, Boolean graderingInnvilget,
                                Kode periodeType, UttakUtsettelseÅrsak utsettelseType, OppholdÅrsak oppholdÅrsak,
                                UttakResultatPeriodeAktivitet gradertAktivitet) {
        this.fom = fom;
        this.tom = tom;
        this.aktiviteter = aktiviteter;
        this.periodeResultatType = periodeResultatType;
        this.begrunnelse = begrunnelse;
        this.periodeResultatÅrsak = periodeResultatÅrsak;
        this.manuellBehandlingÅrsak = manuellBehandlingÅrsak;
        this.graderingAvslagÅrsak = graderingAvslagÅrsak;
        this.flerbarnsdager = flerbarnsdager;
        this.samtidigUttak = samtidigUttak;
        this.samtidigUttaksprosent = samtidigUttaksprosent;
        this.graderingInnvilget = graderingInnvilget;
        this.periodeType = periodeType;
        this.utsettelseType = utsettelseType;
        this.oppholdÅrsak = oppholdÅrsak;
        this.gradertAktivitet = gradertAktivitet;
    }

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public Kode getPeriodeResultatType() {
        return periodeResultatType;
    }

    public void setPeriodeResultatType(Kode periodeResultatType) {
        this.periodeResultatType = periodeResultatType;
    }

    public Kode getPeriodeResultatÅrsak() {
        return periodeResultatÅrsak;
    }

    public void setPeriodeResultatÅrsak(Kode periodeResultatÅrsak) {
        this.periodeResultatÅrsak = periodeResultatÅrsak;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public UttakUtsettelseÅrsak getUtsettelseType() {
        return utsettelseType;
    }

    public void setUtsettelseType(UttakUtsettelseÅrsak utsettelseType) {
        this.utsettelseType = utsettelseType;
    }

    public LocalDate getTom() {
        return tom;
    }

    public void setTom(LocalDate tom) {
        this.tom = tom;
    }

    public LocalDate getFom() {
        return fom;
    }

    public void setFom(LocalDate fom) {
        this.fom = fom;
    }

    public List<UttakResultatPeriodeAktivitet> getAktiviteter() {
        return aktiviteter;
    }

    public void setAktiviteter(List<UttakResultatPeriodeAktivitet> aktiviteter) {
        this.aktiviteter = aktiviteter;
    }

    public Kode getGraderingAvslagÅrsak() {
        return graderingAvslagÅrsak;
    }

    public void setGraderingAvslagÅrsak(Kode graderingAvslagÅrsak) {
        this.graderingAvslagÅrsak = graderingAvslagÅrsak;
    }

    public Boolean getGraderingInnvilget() {
        return graderingInnvilget;
    }

    public void setGraderingInnvilget(Boolean graderingInnvilget) {
        this.graderingInnvilget = graderingInnvilget;
    }

    public Kode getPeriodeType() {
        return periodeType;
    }

    public void setOppholdÅrsak(OppholdÅrsak oppholdÅrsak) {
        this.oppholdÅrsak = oppholdÅrsak;
    }

    public OppholdÅrsak getOppholdÅrsak() {
        return oppholdÅrsak;
    }

    public void setPeriodeType(Kode periodeType) {
        this.periodeType = periodeType;
    }

    public Boolean getFlerbarnsdager() {
        return flerbarnsdager;
    }

    public void setFlerbarnsdager(Boolean flerbarnsdager) {
        this.flerbarnsdager = flerbarnsdager;
    }

    public Boolean getSamtidigUttak() {
        return samtidigUttak;
    }

    public void setSamtidigUttak(Boolean samtidigUttak) {
        this.samtidigUttak = samtidigUttak;
    }

    public Kode getManuellBehandlingÅrsak() {
        return manuellBehandlingÅrsak;
    }

    public void setManuellBehandlingÅrsak(Kode manuellBehandlingÅrsak) {
        this.manuellBehandlingÅrsak = manuellBehandlingÅrsak;
    }

    public BigDecimal getSamtidigUttaksprosent() {
        return samtidigUttaksprosent;
    }

    public void setSamtidigUttaksprosent(BigDecimal samtidigUttaksprosent) {
        this.samtidigUttaksprosent = samtidigUttaksprosent;
    }

    public UttakResultatPeriodeAktivitet getGradertAktivitet() {
        return gradertAktivitet;
    }

    @JsonIgnore
    public BigDecimal getGradertArbeidsprosent() {
        return gradertAktivitet.getProsentArbeid();
    }

    @JsonIgnore
    public void setStønadskonto(Stønadskonto stønadskonto) {
        for (UttakResultatPeriodeAktivitet aktivitet : aktiviteter) {
            aktivitet.setStønadskontoType(stønadskonto);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UttakResultatPeriode periode = (UttakResultatPeriode) o;
        return Objects.equals(fom, periode.fom) &&
                Objects.equals(tom, periode.tom) &&
                Objects.equals(aktiviteter, periode.aktiviteter) &&
                Objects.equals(periodeResultatType, periode.periodeResultatType) &&
                Objects.equals(begrunnelse, periode.begrunnelse) &&
                Objects.equals(periodeResultatÅrsak, periode.periodeResultatÅrsak) &&
                Objects.equals(manuellBehandlingÅrsak, periode.manuellBehandlingÅrsak) &&
                Objects.equals(graderingAvslagÅrsak, periode.graderingAvslagÅrsak) &&
                Objects.equals(flerbarnsdager, periode.flerbarnsdager) &&
                Objects.equals(samtidigUttak, periode.samtidigUttak) &&
                Objects.equals(samtidigUttaksprosent, periode.samtidigUttaksprosent) &&
                Objects.equals(graderingInnvilget, periode.graderingInnvilget) &&
                Objects.equals(periodeType, periode.periodeType) &&
                utsettelseType == periode.utsettelseType &&
                oppholdÅrsak == periode.oppholdÅrsak &&
                Objects.equals(gradertAktivitet, periode.gradertAktivitet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fom, tom, aktiviteter, periodeResultatType, begrunnelse, periodeResultatÅrsak, manuellBehandlingÅrsak, graderingAvslagÅrsak, flerbarnsdager, samtidigUttak, samtidigUttaksprosent, graderingInnvilget, periodeType, utsettelseType, oppholdÅrsak, gradertAktivitet);
    }

    @Override
    public String toString() {
        return "UttakResultatPeriode{" +
                "fom=" + fom +
                ", tom=" + tom +
                ", aktiviteter=" + aktiviteter +
                ", periodeResultatType=" + periodeResultatType +
                ", begrunnelse='" + begrunnelse + '\'' +
                ", periodeResultatÅrsak=" + periodeResultatÅrsak +
                ", manuellBehandlingÅrsak=" + manuellBehandlingÅrsak +
                ", graderingAvslagÅrsak=" + graderingAvslagÅrsak +
                ", flerbarnsdager=" + flerbarnsdager +
                ", samtidigUttak=" + samtidigUttak +
                ", samtidigUttaksprosent=" + samtidigUttaksprosent +
                ", graderingInnvilget=" + graderingInnvilget +
                ", periodeType=" + periodeType +
                ", utsettelseType=" + utsettelseType +
                ", oppholdÅrsak=" + oppholdÅrsak +
                ", gradertAktivitet=" + gradertAktivitet +
                '}';
    }
}
