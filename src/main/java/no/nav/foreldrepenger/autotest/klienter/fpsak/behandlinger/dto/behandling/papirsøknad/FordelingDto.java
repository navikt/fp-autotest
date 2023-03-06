package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.util.ArrayList;
import java.util.List;

public class FordelingDto {

    public List<PermisjonPeriodeDto> permisjonsPerioder = new ArrayList<>();
    public List<GraderingPeriodeDto> graderingPeriode = new ArrayList<>();
    public List<UtsettelsePeriodeDto> utsettelsePeriode = new ArrayList<>();
}
