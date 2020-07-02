package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BeregningsgrunnlagArbeidsforholdDto {
    private String arbeidsgiverNavn;
    private String arbeidsgiverId;
    private String startdato;
    private String opphoersdato;
    private String arbeidsforholdId;
    private Kode arbeidsforholdType;
    private AktørId aktørId;
    private double refusjonPrAar;

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public void setArbeidsgiverNavn(String arbeidsgiverNavn) {
        this.arbeidsgiverNavn = arbeidsgiverNavn;
    }

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }

    public void setArbeidsgiverId(String arbeidsgiverId) {
        this.arbeidsgiverId = arbeidsgiverId;
    }

    public String getStartdato() {
        return startdato;
    }

    public void setStartdato(String startdato) {
        this.startdato = startdato;
    }

    public String getOpphoersdato() {
        return opphoersdato;
    }

    public void setOpphoersdato(String opphoersdato) {
        this.opphoersdato = opphoersdato;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public void setArbeidsforholdId(String arbeidsforholdId) {
        this.arbeidsforholdId = arbeidsforholdId;
    }

    public Kode getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public void setArbeidsforholdType(Kode arbeidsforholdType) {
        this.arbeidsforholdType = arbeidsforholdType;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public void setAktørId(AktørId aktørId) {
        this.aktørId = aktørId;
    }

    public double getRefusjonPrAar() {
        return refusjonPrAar;
    }

    public void setRefusjonPrAar(double refusjonPrAar) {
        this.refusjonPrAar = refusjonPrAar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeregningsgrunnlagArbeidsforholdDto that = (BeregningsgrunnlagArbeidsforholdDto) o;
        return Double.compare(that.refusjonPrAar, refusjonPrAar) == 0 &&
                Objects.equals(arbeidsgiverNavn, that.arbeidsgiverNavn) &&
                Objects.equals(arbeidsgiverId, that.arbeidsgiverId) &&
                Objects.equals(startdato, that.startdato) &&
                Objects.equals(opphoersdato, that.opphoersdato) &&
                Objects.equals(arbeidsforholdId, that.arbeidsforholdId) &&
                Objects.equals(arbeidsforholdType, that.arbeidsforholdType) &&
                Objects.equals(aktørId, that.aktørId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arbeidsgiverNavn, arbeidsgiverId, startdato, opphoersdato, arbeidsforholdId, arbeidsforholdType, aktørId, refusjonPrAar);
    }

    @Override
    public String toString() {
        return "BeregningsgrunnlagArbeidsforholdDto{" +
                "arbeidsgiverNavn='" + arbeidsgiverNavn + '\'' +
                ", arbeidsgiverId='" + arbeidsgiverId + '\'' +
                ", startdato='" + startdato + '\'' +
                ", opphoersdato='" + opphoersdato + '\'' +
                ", arbeidsforholdId='" + arbeidsforholdId + '\'' +
                ", arbeidsforholdType=" + arbeidsforholdType +
                ", aktørId=" + aktørId +
                ", refusjonPrAar=" + refusjonPrAar +
                '}';
    }
}
