package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;

public class PermisjonPeriodeDto {

    public LocalDate periodeFom;

    public LocalDate periodeTom;

    public StønadskontoType periodeType;

    public PermisjonPeriodeDto(StønadskontoType stønadskonto, LocalDate fom, LocalDate tom) {
        this.periodeType = stønadskonto;
        this.periodeFom = fom;
        this.periodeTom = tom;
    }
}
