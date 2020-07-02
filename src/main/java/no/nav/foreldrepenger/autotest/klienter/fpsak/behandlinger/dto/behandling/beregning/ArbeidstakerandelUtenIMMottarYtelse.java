package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ArbeidstakerandelUtenIMMottarYtelse {

    private long andelsnr;
    private Boolean mottarYtelse;

    public ArbeidstakerandelUtenIMMottarYtelse(long andelsnr, Boolean mottarYtelse) {
        this.andelsnr = andelsnr;
        this.mottarYtelse = mottarYtelse;
    }

    public long getAndelsnr() {
        return andelsnr;
    }

    public Boolean getMottarYtelse() {
        return mottarYtelse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArbeidstakerandelUtenIMMottarYtelse that = (ArbeidstakerandelUtenIMMottarYtelse) o;
        return andelsnr == that.andelsnr &&
                Objects.equals(mottarYtelse, that.mottarYtelse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andelsnr, mottarYtelse);
    }

    @Override
    public String toString() {
        return "ArbeidstakerandelUtenIMMottarYtelse{" +
                "andelsnr=" + andelsnr +
                ", mottarYtelse=" + mottarYtelse +
                '}';
    }
}
