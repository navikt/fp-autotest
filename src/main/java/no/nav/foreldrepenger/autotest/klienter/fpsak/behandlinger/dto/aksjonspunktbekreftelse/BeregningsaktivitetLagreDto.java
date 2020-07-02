package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BeregningsaktivitetLagreDto {

    private Kode opptjeningAktivitetType;
    private LocalDate fom;
    private LocalDate tom;
    private String oppdragsgiverOrg;
    private String arbeidsgiverIdentifikator;
    private String arbeidsforholdRef;
    private Boolean skalBrukes;

    public BeregningsaktivitetLagreDto(Kode opptjeningAktivitetType, LocalDate fom, LocalDate tom,
            String oppdragsgiverOrg, String arbeidsgiverIdentifikator, String arbeidsforholdRef, boolean skalBrukes) {
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

    public void setSkalBrukes(Boolean skalBrukes) {
        this.skalBrukes = skalBrukes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeregningsaktivitetLagreDto that = (BeregningsaktivitetLagreDto) o;
        return Objects.equals(opptjeningAktivitetType, that.opptjeningAktivitetType) &&
                Objects.equals(fom, that.fom) &&
                Objects.equals(tom, that.tom) &&
                Objects.equals(oppdragsgiverOrg, that.oppdragsgiverOrg) &&
                Objects.equals(arbeidsgiverIdentifikator, that.arbeidsgiverIdentifikator) &&
                Objects.equals(arbeidsforholdRef, that.arbeidsforholdRef) &&
                Objects.equals(skalBrukes, that.skalBrukes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opptjeningAktivitetType, fom, tom, oppdragsgiverOrg, arbeidsgiverIdentifikator, arbeidsforholdRef, skalBrukes);
    }

    @Override
    public String toString() {
        return "BeregningsaktivitetLagreDto{" +
                "opptjeningAktivitetType=" + opptjeningAktivitetType +
                ", fom=" + fom +
                ", tom=" + tom +
                ", oppdragsgiverOrg='" + oppdragsgiverOrg + '\'' +
                ", arbeidsgiverIdentifikator='" + arbeidsgiverIdentifikator + '\'' +
                ", arbeidsforholdRef='" + arbeidsforholdRef + '\'' +
                ", skalBrukes=" + skalBrukes +
                '}';
    }
}
