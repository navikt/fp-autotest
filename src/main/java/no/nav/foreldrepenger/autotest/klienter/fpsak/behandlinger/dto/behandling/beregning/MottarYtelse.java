package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MottarYtelse {

    private Boolean frilansMottarYtelse;
    private List<ArbeidstakerandelUtenIMMottarYtelse> arbeidstakerUtenIMMottarYtelse;

    public MottarYtelse(Boolean frilansMottarYtelse,
                        List<ArbeidstakerandelUtenIMMottarYtelse> arbeidstakerandelUtenIMMottarYtelses) {
        this.frilansMottarYtelse = frilansMottarYtelse;
        this.arbeidstakerUtenIMMottarYtelse = arbeidstakerandelUtenIMMottarYtelses;
    }

    public Boolean getFrilansMottarYtelse() {
        return frilansMottarYtelse;
    }

    public void setFrilansMottarYtelse(boolean frilansMottarYtelse) {
        this.frilansMottarYtelse = frilansMottarYtelse;
    }

    public List<ArbeidstakerandelUtenIMMottarYtelse> getArbeidstakerUtenIMMottarYtelse() {
        return arbeidstakerUtenIMMottarYtelse;
    }

    public void leggTilArbeidstakerandelUtenIMMottarYtelse(
            ArbeidstakerandelUtenIMMottarYtelse arbeidstakerandelUtenIMMottarYtelse) {
        this.arbeidstakerUtenIMMottarYtelse.add(arbeidstakerandelUtenIMMottarYtelse);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MottarYtelse that = (MottarYtelse) o;
        return Objects.equals(frilansMottarYtelse, that.frilansMottarYtelse) &&
                Objects.equals(arbeidstakerUtenIMMottarYtelse, that.arbeidstakerUtenIMMottarYtelse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(frilansMottarYtelse, arbeidstakerUtenIMMottarYtelse);
    }

    @Override
    public String toString() {
        return "MottarYtelse{" +
                "frilansMottarYtelse=" + frilansMottarYtelse +
                ", arbeidstakerUtenIMMottarYtelse=" + arbeidstakerUtenIMMottarYtelse +
                '}';
    }
}
