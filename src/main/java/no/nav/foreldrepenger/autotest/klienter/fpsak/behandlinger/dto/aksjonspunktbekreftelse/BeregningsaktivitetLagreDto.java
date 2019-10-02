package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeregningsaktivitetLagreDto {

    protected Kode opptjeningAktivitetType;

    protected LocalDate fom;

    protected LocalDate tom;

    protected String oppdragsgiverOrg;

    protected String arbeidsgiverIdentifikator;

    protected String arbeidsforholdRef;

    protected Boolean skalBrukes;

    public BeregningsaktivitetLagreDto(Kode opptjeningAktivitetType, LocalDate fom, LocalDate tom, String oppdragsgiverOrg, String arbeidsgiverIdentifikator, String arbeidsforholdRef, boolean skalBrukes) {
        this.opptjeningAktivitetType = opptjeningAktivitetType;
        this.fom = fom;
        this.tom = tom;
        this.oppdragsgiverOrg = oppdragsgiverOrg;
        this.arbeidsgiverIdentifikator = arbeidsgiverIdentifikator;
        this.arbeidsforholdRef = arbeidsforholdRef;
        this.skalBrukes = skalBrukes;
    }


    public Kode getOpptjeningAktivitetType() {
        return opptjeningAktivitetType;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public String getOppdragsgiverOrg() {
        return oppdragsgiverOrg;
    }

    public String getArbeidsgiverIdentifikator() {
        return arbeidsgiverIdentifikator;
    }

    public String getArbeidsforholdRef() {
        return arbeidsforholdRef;
    }

    public Boolean getSkalBrukes() {
        return skalBrukes;
    }
}
