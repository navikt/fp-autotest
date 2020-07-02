package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderteArbeidsforholdDto {

    private Long andelsnr;
    private boolean tidsbegrensetArbeidsforhold;
    private Boolean opprinneligVerdi;

    public VurderteArbeidsforholdDto(Long andelsnr, boolean tidsbegrensetArbeidsforhold, Boolean opprinneligVerdi) {
        this.andelsnr = andelsnr;
        this.tidsbegrensetArbeidsforhold = tidsbegrensetArbeidsforhold;
        this.opprinneligVerdi = opprinneligVerdi;
    }

    public Long getAndelsnr() {
        return andelsnr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderteArbeidsforholdDto that = (VurderteArbeidsforholdDto) o;
        return tidsbegrensetArbeidsforhold == that.tidsbegrensetArbeidsforhold &&
                Objects.equals(andelsnr, that.andelsnr) &&
                Objects.equals(opprinneligVerdi, that.opprinneligVerdi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andelsnr, tidsbegrensetArbeidsforhold, opprinneligVerdi);
    }

    @Override
    public String toString() {
        return "VurderteArbeidsforholdDto{" +
                "andelsnr=" + andelsnr +
                ", tidsbegrensetArbeidsforhold=" + tidsbegrensetArbeidsforhold +
                ", opprinneligVerdi=" + opprinneligVerdi +
                '}';
    }
}
