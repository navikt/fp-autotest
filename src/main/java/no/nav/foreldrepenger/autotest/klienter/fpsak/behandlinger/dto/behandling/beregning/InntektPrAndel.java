package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class InntektPrAndel {

    private Integer inntekt;
    private Long andelsnr;

    public InntektPrAndel(Integer inntekt, Long andelsnr) {
        super();
        this.inntekt = inntekt;
        this.andelsnr = andelsnr;
    }

    public Integer getInntekt() {
        return inntekt;
    }

    public Long getAndelsnr() {
        return andelsnr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InntektPrAndel that = (InntektPrAndel) o;
        return Objects.equals(inntekt, that.inntekt) &&
                Objects.equals(andelsnr, that.andelsnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inntekt, andelsnr);
    }

    @Override
    public String toString() {
        return "InntektPrAndel{" +
                "inntekt=" + inntekt +
                ", andelsnr=" + andelsnr +
                '}';
    }
}
