package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UtsettelsePeriodeDto {

    public final LocalDate periodeFom;
    public final LocalDate periodeTom;
    public final SøknadUtsettelseÅrsak arsakForUtsettelse;

    public UtsettelsePeriodeDto(LocalDate periodeFom, LocalDate periodeTom, SøknadUtsettelseÅrsak arsakForUtsettelse) {
        this.periodeFom = periodeFom;
        this.periodeTom = periodeTom;
        this.arsakForUtsettelse = arsakForUtsettelse;
    }

    public LocalDate getPeriodeFom() {
        return periodeFom;
    }

    public LocalDate getPeriodeTom() {
        return periodeTom;
    }

    public SøknadUtsettelseÅrsak getArsakForUtsettelse() {
        return arsakForUtsettelse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UtsettelsePeriodeDto that = (UtsettelsePeriodeDto) o;
        return Objects.equals(periodeFom, that.periodeFom) &&
                Objects.equals(periodeTom, that.periodeTom) &&
                arsakForUtsettelse == that.arsakForUtsettelse;
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodeFom, periodeTom, arsakForUtsettelse);
    }

    @Override
    public String toString() {
        return "UtsettelsePeriodeDto{" +
                "periodeFom=" + periodeFom +
                ", periodeTom=" + periodeTom +
                ", arsakForUtsettelse=" + arsakForUtsettelse +
                '}';
    }
}
