package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.opptjening;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpptjeningAktivitet {

    protected String aktivitetType;
    protected String arbeidsforholdRef;
    protected String arbeidsgiver;
    protected String arbeidsgiverIdentifikator;
    protected String arbeidsgiverReferanse;
    protected String begrunnelse;

    protected LocalDate originalFom;
    protected LocalDate originalTom;
    protected LocalDate opptjeningFom;
    protected LocalDate opptjeningTom;
    protected boolean erEndret;
    protected boolean erGodkjent;
    protected boolean erManueltOpprettet;
    protected String oppdragsgiverOrg;

    public String getOppdragsgiverOrg() {
        return oppdragsgiverOrg;
    }

    public void setOppdragsgiverOrg(String oppdragsgiverOrg) {
        this.oppdragsgiverOrg = oppdragsgiverOrg;
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public void setArbeidsgiverReferanse(String arbeidsgiverReferanse) {
        this.arbeidsgiverReferanse = arbeidsgiverReferanse;
    }

    public String getAktivitetType() {
        return aktivitetType;
    }

    public void setAktivitetType(String aktivitetType) {
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

}
