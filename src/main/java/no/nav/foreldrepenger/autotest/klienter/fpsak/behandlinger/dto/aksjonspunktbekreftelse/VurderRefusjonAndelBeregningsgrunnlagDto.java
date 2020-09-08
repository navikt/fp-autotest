package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderRefusjonAndelBeregningsgrunnlagDto {

    private String arbeidsgiverOrgnr;
    private String arbeidsgiverAktoerId;
    private String internArbeidsforholdRef;
    private LocalDate fastsattRefusjonFom;

    public VurderRefusjonAndelBeregningsgrunnlagDto(String arbeidsgiverOrgnr,
                                                    String arbeidsgiverAktoerId,
                                                    String internArbeidsforholdRef,
                                                    LocalDate fastsattRefusjonFom) {
        this.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
        this.arbeidsgiverAktoerId = arbeidsgiverAktoerId;
        this.internArbeidsforholdRef = internArbeidsforholdRef;
        this.fastsattRefusjonFom = fastsattRefusjonFom;
    }

    public String getArbeidsgiverOrgnr() {
        return arbeidsgiverOrgnr;
    }

    public String getArbeidsgiverAktoerId() {
        return arbeidsgiverAktoerId;
    }

    public String getInternArbeidsforholdRef() {
        return internArbeidsforholdRef;
    }

    public LocalDate getFastsattRefusjonFom() {
        return fastsattRefusjonFom;
    }

    public void setArbeidsgiverAktoerId(String arbeidsgiverAktoerId) {
        this.arbeidsgiverAktoerId = arbeidsgiverAktoerId;
    }

    public void setFastsattRefusjonFom(LocalDate fastsattRefusjonFom) {
        this.fastsattRefusjonFom = fastsattRefusjonFom;
    }
}
