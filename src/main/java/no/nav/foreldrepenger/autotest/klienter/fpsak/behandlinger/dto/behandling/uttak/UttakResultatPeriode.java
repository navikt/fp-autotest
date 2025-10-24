package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakresultatUtsettelseÅrsak;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class UttakResultatPeriode implements Serializable {

    protected LocalDate fom;
    protected LocalDate tom;
    protected List<UttakResultatPeriodeAktivitet> aktiviteter;
    protected PeriodeResultatType periodeResultatType;
    protected String begrunnelse;
    protected PeriodeResultatÅrsak periodeResultatÅrsak;
    protected String manuellBehandlingÅrsak;
    protected String graderingAvslagÅrsak;
    protected Boolean flerbarnsdager;
    protected Boolean samtidigUttak;
    protected BigDecimal samtidigUttaksprosent;
    protected Boolean graderingInnvilget;
    protected String periodeType;
    protected UttakresultatUtsettelseÅrsak utsettelseType;
    protected OppholdÅrsak oppholdÅrsak;
    protected UttakResultatPeriodeAktivitet gradertAktivitet = null;

    public void setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
    }

    public PeriodeResultatType getPeriodeResultatType() {
        return periodeResultatType;
    }

    public void setPeriodeResultatType(PeriodeResultatType periodeResultatType) {
        this.periodeResultatType = periodeResultatType;
    }

    public PeriodeResultatÅrsak getPeriodeResultatÅrsak() {
        return periodeResultatÅrsak;
    }

    public void setPeriodeResultatÅrsak(PeriodeResultatÅrsak periodeResultatÅrsak) {
        this.periodeResultatÅrsak = periodeResultatÅrsak;
    }

    public UttakresultatUtsettelseÅrsak getUtsettelseType() {
        return utsettelseType;
    }

    public void setUtsettelseType(UttakresultatUtsettelseÅrsak utsettelseType) {
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

    public String getGraderingAvslagÅrsak() {
        return graderingAvslagÅrsak;
    }

    public void setGraderingAvslagÅrsak(String graderingAvslagÅrsak) {
        this.graderingAvslagÅrsak = graderingAvslagÅrsak;
    }

    public Boolean getGraderingInnvilget() {
        return graderingInnvilget;
    }

    public void setGraderingInnvilget(Boolean graderingInnvilget) {
        this.graderingInnvilget = graderingInnvilget;
    }

    public BigDecimal getGradertArbeidsprosent() {
        return gradertAktivitet.prosentArbeid;
    }

    public void setOppholdÅrsak(OppholdÅrsak oppholdÅrsak) {
        this.oppholdÅrsak = oppholdÅrsak;
    }

    public OppholdÅrsak getOppholdÅrsak() {
        return oppholdÅrsak;
    }

    public String getPeriodeType() {
        return periodeType;
    }

    public void setPeriodeType(String periodeType) {
        this.periodeType = periodeType;
    }

    public void setStønadskonto(KontoType stønadskonto) {
        for (UttakResultatPeriodeAktivitet aktivitet : aktiviteter) {
            aktivitet.setKontotype(stønadskonto);
        }
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

    public String getManuellBehandlingÅrsak() {
        return manuellBehandlingÅrsak;
    }

    public void setManuellBehandlingÅrsak(String manuellBehandlingÅrsak) {
        this.manuellBehandlingÅrsak = manuellBehandlingÅrsak;
    }

    public BigDecimal getSamtidigUttaksprosent() {
        return samtidigUttaksprosent;
    }

    public void setSamtidigUttaksprosent(BigDecimal samtidigUttaksprosent) {
        this.samtidigUttaksprosent = samtidigUttaksprosent;
    }
}
