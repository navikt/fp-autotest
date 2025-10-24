package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType;

public class PermisjonPeriodeDto {

    public LocalDate periodeFom;

    public LocalDate periodeTom;

    public KontoType periodeType;

    public PermisjonPeriodeDto(KontoType stønadskonto, LocalDate fom, LocalDate tom) {
        this.periodeType = stønadskonto;
        this.periodeFom = fom;
        this.periodeTom = tom;
    }
}
