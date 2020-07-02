package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettMaanedsinntektUtenInntektsmeldingAndel {

    private long andelsnr;
    private Integer fastsattBeløp;

    public FastsettMaanedsinntektUtenInntektsmeldingAndel(long andelsnr, int arbeidsinntekt) {
        this.andelsnr = andelsnr;
        this.fastsattBeløp = arbeidsinntekt;
    }

    public long getAndelsnr() {
        return andelsnr;
    }

    public Integer getFastsattBeløp() {
        return fastsattBeløp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettMaanedsinntektUtenInntektsmeldingAndel that = (FastsettMaanedsinntektUtenInntektsmeldingAndel) o;
        return andelsnr == that.andelsnr &&
                Objects.equals(fastsattBeløp, that.fastsattBeløp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andelsnr, fastsattBeløp);
    }

    @Override
    public String toString() {
        return "FastsettMaanedsinntektUtenInntektsmeldingAndel{" +
                "andelsnr=" + andelsnr +
                ", fastsattBeløp=" + fastsattBeløp +
                '}';
    }
}
