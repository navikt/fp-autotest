package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.opptjening;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OpptjeningAktivitet {

    protected Kode aktivitetType;
    protected String arbeidsforholdRef;
    protected String arbeidsgiver;
    protected String arbeidsgiverIdentifikator;
    protected String begrunnelse;

    protected LocalDate originalFom;
    protected LocalDate originalTom;
    protected LocalDate opptjeningFom;
    protected LocalDate opptjeningTom;
    protected boolean erEndret;
    protected boolean erGodkjent;
    protected boolean erManueltOpprettet;
    protected String oppdragsgiverOrg;

    public OpptjeningAktivitet(Kode aktivitetType, String arbeidsforholdRef, String arbeidsgiver,
                               String arbeidsgiverIdentifikator, String begrunnelse, LocalDate originalFom,
                               LocalDate originalTom, LocalDate opptjeningFom, LocalDate opptjeningTom,
                               boolean erEndret, boolean erGodkjent, boolean erManueltOpprettet,
                               String oppdragsgiverOrg) {
        this.aktivitetType = aktivitetType;
        this.arbeidsforholdRef = arbeidsforholdRef;
        this.arbeidsgiver = arbeidsgiver;
        this.arbeidsgiverIdentifikator = arbeidsgiverIdentifikator;
        this.begrunnelse = begrunnelse;
        this.originalFom = originalFom;
        this.originalTom = originalTom;
        this.opptjeningFom = opptjeningFom;
        this.opptjeningTom = opptjeningTom;
        this.erEndret = erEndret;
        this.erGodkjent = erGodkjent;
        this.erManueltOpprettet = erManueltOpprettet;
        this.oppdragsgiverOrg = oppdragsgiverOrg;
    }

    public String getArbeidsgiver() {
        return arbeidsgiver;
    }

    public String getArbeidsgiverIdentifikator() {
        return arbeidsgiverIdentifikator;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    public LocalDate getOriginalFom() {
        return originalFom;
    }

    public LocalDate getOriginalTom() {
        return originalTom;
    }

    public boolean isErEndret() {
        return erEndret;
    }

    public boolean isErGodkjent() {
        return erGodkjent;
    }

    public String getOppdragsgiverOrg() {
        return oppdragsgiverOrg;
    }

    public void setOppdragsgiverOrg(String oppdragsgiverOrg) {
        this.oppdragsgiverOrg = oppdragsgiverOrg;
    }

    public Kode getAktivitetType() {
        return aktivitetType;
    }

    public void setAktivitetType(Kode aktivitetType) {
        this.aktivitetType = aktivitetType;
    }

    public String getArbeidsforholdRef() {
        return arbeidsforholdRef;
    }

    public void setArbeidsforholdRef(String arbeidsforholdRef) {
        this.arbeidsforholdRef = arbeidsforholdRef;
    }

    public LocalDate getOpptjeningFom() {
        return opptjeningFom;
    }

    public LocalDate getOpptjeningTom() {
        return opptjeningTom;
    }

    public void setOriginalFom(LocalDate fom) {
        originalFom = fom;
    }

    public void setOriginalTom(LocalDate tom) {
        originalTom = tom;
    }

    public boolean isErManueltOpprettet() {
        return erManueltOpprettet;
    }

    public void setErManueltOpprettet(boolean erManueltOpprettet) {
        this.erManueltOpprettet = erManueltOpprettet;
    }

    public void vurder(boolean erGodkjent, String begrunnelse, boolean erManueltOpprettet) {
        this.erGodkjent = erGodkjent;
        this.begrunnelse = begrunnelse;
        this.erEndret = true;
        this.setErManueltOpprettet(erManueltOpprettet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpptjeningAktivitet that = (OpptjeningAktivitet) o;
        return erEndret == that.erEndret &&
                erGodkjent == that.erGodkjent &&
                erManueltOpprettet == that.erManueltOpprettet &&
                Objects.equals(aktivitetType, that.aktivitetType) &&
                Objects.equals(arbeidsforholdRef, that.arbeidsforholdRef) &&
                Objects.equals(arbeidsgiver, that.arbeidsgiver) &&
                Objects.equals(arbeidsgiverIdentifikator, that.arbeidsgiverIdentifikator) &&
                Objects.equals(begrunnelse, that.begrunnelse) &&
                Objects.equals(originalFom, that.originalFom) &&
                Objects.equals(originalTom, that.originalTom) &&
                Objects.equals(opptjeningFom, that.opptjeningFom) &&
                Objects.equals(opptjeningTom, that.opptjeningTom) &&
                Objects.equals(oppdragsgiverOrg, that.oppdragsgiverOrg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktivitetType, arbeidsforholdRef, arbeidsgiver, arbeidsgiverIdentifikator, begrunnelse, originalFom, originalTom, opptjeningFom, opptjeningTom, erEndret, erGodkjent, erManueltOpprettet, oppdragsgiverOrg);
    }

    @Override
    public String toString() {
        return "OpptjeningAktivitet{" +
                "aktivitetType=" + aktivitetType +
                ", arbeidsforholdRef='" + arbeidsforholdRef + '\'' +
                ", arbeidsgiver='" + arbeidsgiver + '\'' +
                ", arbeidsgiverIdentifikator='" + arbeidsgiverIdentifikator + '\'' +
                ", begrunnelse='" + begrunnelse + '\'' +
                ", originalFom=" + originalFom +
                ", originalTom=" + originalTom +
                ", opptjeningFom=" + opptjeningFom +
                ", opptjeningTom=" + opptjeningTom +
                ", erEndret=" + erEndret +
                ", erGodkjent=" + erGodkjent +
                ", erManueltOpprettet=" + erManueltOpprettet +
                ", oppdragsgiverOrg='" + oppdragsgiverOrg + '\'' +
                '}';
    }
}
