package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderTidsbegrensetArbeidsforholdDto {

    private List<VurderteArbeidsforholdDto> fastsatteArbeidsforhold;

    public VurderTidsbegrensetArbeidsforholdDto(@JsonProperty("fastsatteArbeidsforhold")
                                                        List<VurderteArbeidsforholdDto> fastsatteArbeidsforhold) {
        this.fastsatteArbeidsforhold = fastsatteArbeidsforhold;
    }

    public List<VurderteArbeidsforholdDto> getFastsatteArbeidsforhold() {
        return fastsatteArbeidsforhold;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderTidsbegrensetArbeidsforholdDto that = (VurderTidsbegrensetArbeidsforholdDto) o;
        return Objects.equals(fastsatteArbeidsforhold, that.fastsatteArbeidsforhold);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fastsatteArbeidsforhold);
    }

    @Override
    public String toString() {
        return "VurderTidsbegrensetArbeidsforholdDto{" +
                "fastsatteArbeidsforhold=" + fastsatteArbeidsforhold +
                '}';
    }
}
