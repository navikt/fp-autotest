package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class PermisjonPeriodeDto {

    private Stønadskonto periodeType;
    private LocalDate periodeFom;
    private LocalDate periodeTom;

    public PermisjonPeriodeDto(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom) {
        this.periodeType = stønadskonto;
        this.periodeFom = fom;
        this.periodeTom = tom;
    }

    public LocalDate getPeriodeFom() {
        return periodeFom;
    }

    public LocalDate getPeriodeTom() {
        return periodeTom;
    }

    public Stønadskonto getPeriodeType() {
        return periodeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermisjonPeriodeDto that = (PermisjonPeriodeDto) o;
        return Objects.equals(periodeFom, that.periodeFom) &&
                Objects.equals(periodeTom, that.periodeTom) &&
                periodeType == that.periodeType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(periodeFom, periodeTom, periodeType);
    }

    @Override
    public String toString() {
        return "PermisjonPeriodeDto{" +
                "periodeFom=" + periodeFom +
                ", periodeTom=" + periodeTom +
                ", periodeType=" + periodeType +
                '}';
    }
}
