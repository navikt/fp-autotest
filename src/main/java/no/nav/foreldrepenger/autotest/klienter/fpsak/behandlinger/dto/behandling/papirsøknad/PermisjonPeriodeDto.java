package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;

public class PermisjonPeriodeDto {

    public LocalDate periodeFom;

    public LocalDate periodeTom;

    public Stønadskonto periodeType;

    public PermisjonPeriodeDto(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom) {
        this.periodeType = stønadskonto;
        this.periodeFom = fom;
        this.periodeTom = tom;
    }
}
