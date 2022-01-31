package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.OppholdÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.IkkeOppfyltÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.InnvilgetÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Kode;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UttakResultatPeriode implements Serializable {

    protected LocalDate fom;
    protected LocalDate tom;
    protected List<UttakResultatPeriodeAktivitet> aktiviteter;
    protected PeriodeResultatType periodeResultatType;
    protected String begrunnelse;
    protected PeriodeResultatÅrsak periodeResultatÅrsak;
    protected InnvilgetÅrsak innvilgetÅrsak;
    protected IkkeOppfyltÅrsak ikkeOppfyltÅrsak;
    protected Kode manuellBehandlingÅrsak;
    protected Kode graderingAvslagÅrsak;
    protected Boolean flerbarnsdager;
    protected Boolean samtidigUttak;
    protected BigDecimal samtidigUttaksprosent;
    protected Boolean graderingInnvilget;
    protected Kode periodeType;
    protected UttakUtsettelseÅrsak utsettelseType;
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
        if (innvilgetÅrsak != null && !InnvilgetÅrsak.UKJENT.equals(innvilgetÅrsak)) {
            return innvilgetÅrsak;
        } else if (ikkeOppfyltÅrsak != null && !IkkeOppfyltÅrsak.UKJENT.equals(ikkeOppfyltÅrsak)) {
            return ikkeOppfyltÅrsak;
        }
        return periodeResultatÅrsak;
    }

    public void setPeriodeResultatÅrsak(PeriodeResultatÅrsak periodeResultatÅrsak) {
        this.periodeResultatÅrsak = periodeResultatÅrsak;
    }

    public InnvilgetÅrsak getInnvilgetÅrsak() {
        return innvilgetÅrsak;
    }

    public void setInnvilgetÅrsak(InnvilgetÅrsak innvilgetÅrsak) {
        this.innvilgetÅrsak = innvilgetÅrsak;
    }

    public IkkeOppfyltÅrsak getIkkeOppfyltÅrsak() {
        return ikkeOppfyltÅrsak;
    }

    public void setIkkeOppfyltÅrsak(IkkeOppfyltÅrsak ikkeOppfyltÅrsak) {
        this.ikkeOppfyltÅrsak = ikkeOppfyltÅrsak;
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

    public BigDecimal getGradertArbeidsprosent() {
        return gradertAktivitet.prosentArbeid;
    }

    public void setOppholdÅrsak(OppholdÅrsak oppholdÅrsak) {
        this.oppholdÅrsak = oppholdÅrsak;
    }

    public OppholdÅrsak getOppholdÅrsak() {
        return oppholdÅrsak;
    }

    public Kode getPeriodeType() {
        return periodeType;
    }

    public void setPeriodeType(Kode periodeType) {
        this.periodeType = periodeType;
    }

    public void setStønadskonto(StønadskontoType stønadskonto) {
        for (UttakResultatPeriodeAktivitet aktivitet : aktiviteter) {
            aktivitet.setStønadskontoType(stønadskonto);
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
}
