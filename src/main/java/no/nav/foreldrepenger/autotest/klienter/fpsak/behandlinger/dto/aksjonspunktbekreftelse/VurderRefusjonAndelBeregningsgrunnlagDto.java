package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.modell.felles.Orgnummer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VurderRefusjonAndelBeregningsgrunnlagDto {

    private final Orgnummer arbeidsgiverOrgnr;
    private String arbeidsgiverAktoerId;
    private final String internArbeidsforholdRef;
    private LocalDate fastsattRefusjonFom;

    public VurderRefusjonAndelBeregningsgrunnlagDto(Orgnummer arbeidsgiverOrgnr,
                                                    String arbeidsgiverAktoerId,
                                                    String internArbeidsforholdRef,
                                                    LocalDate fastsattRefusjonFom) {
        this.arbeidsgiverOrgnr = arbeidsgiverOrgnr;
        this.arbeidsgiverAktoerId = arbeidsgiverAktoerId;
        this.internArbeidsforholdRef = internArbeidsforholdRef;
        this.fastsattRefusjonFom = fastsattRefusjonFom;
    }

    public Orgnummer getArbeidsgiverOrgnr() {
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
