package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FordelingDto {

    public List<PermisjonPeriodeDto> permisjonsPerioder = new ArrayList<>();
    public List<GraderingPeriodeDto> graderingPeriode = new ArrayList<>();
    public List<UtsettelsePeriodeDto> utsettelsePeriode = new ArrayList<>();

    public FordelingDto() {
        super();
    }

    @JsonCreator
    public FordelingDto(List<PermisjonPeriodeDto> permisjonsPerioder, List<GraderingPeriodeDto> graderingPeriode,
                        List<UtsettelsePeriodeDto> utsettelsePeriode) {
        this.permisjonsPerioder = permisjonsPerioder;
        this.graderingPeriode = graderingPeriode;
        this.utsettelsePeriode = utsettelsePeriode;
    }

    public List<PermisjonPeriodeDto> getPermisjonsPerioder() {
        return permisjonsPerioder;
    }

    public List<GraderingPeriodeDto> getGraderingPeriode() {
        return graderingPeriode;
    }

    public List<UtsettelsePeriodeDto> getUtsettelsePeriode() {
        return utsettelsePeriode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FordelingDto that = (FordelingDto) o;
        return Objects.equals(permisjonsPerioder, that.permisjonsPerioder) &&
                Objects.equals(graderingPeriode, that.graderingPeriode) &&
                Objects.equals(utsettelsePeriode, that.utsettelsePeriode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(permisjonsPerioder, graderingPeriode, utsettelsePeriode);
    }

    @Override
    public String toString() {
        return "FordelingDto{" +
                "permisjonsPerioder=" + permisjonsPerioder +
                ", graderingPeriode=" + graderingPeriode +
                ", utsettelsePeriode=" + utsettelsePeriode +
                '}';
    }
}
