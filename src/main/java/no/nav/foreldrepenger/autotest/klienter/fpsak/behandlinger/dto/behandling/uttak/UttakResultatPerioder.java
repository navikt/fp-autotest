package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UttakResultatPerioder {
    private List<UttakResultatPeriode> perioderAnnenpart;
    private List<UttakResultatPeriode> perioderSøker;
//    private boolean annenForelderHarRett;
//    private boolean aleneomsorg;

    public UttakResultatPerioder(List<UttakResultatPeriode> perioderAnnenpart, List<UttakResultatPeriode> perioderSøker) {
        this.perioderAnnenpart = perioderAnnenpart;
        this.perioderSøker = perioderSøker;
    }

    public List<UttakResultatPeriode> getPerioderAnnenpart() {
        return perioderAnnenpart;
    }

    public List<UttakResultatPeriode> getPerioderSøker() {
        return perioderSøker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UttakResultatPerioder that = (UttakResultatPerioder) o;
        return Objects.equals(perioderAnnenpart, that.perioderAnnenpart) &&
                Objects.equals(perioderSøker, that.perioderSøker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(perioderAnnenpart, perioderSøker);
    }

    @Override
    public String toString() {
        return "UttakResultatPerioder{" +
                "perioderAnnenpart=" + perioderAnnenpart +
                ", perioderSøker=" + perioderSøker +
                '}';
    }
}
