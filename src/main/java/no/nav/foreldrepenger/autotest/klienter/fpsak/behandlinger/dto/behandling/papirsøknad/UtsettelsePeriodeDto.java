package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UtsettelseÅrsak;


public class UtsettelsePeriodeDto {

    public final LocalDate periodeFom;
    public final LocalDate periodeTom;
    public final UtsettelseÅrsak arsakForUtsettelse;

    public UtsettelsePeriodeDto(LocalDate periodeFom, LocalDate periodeTom, UtsettelseÅrsak arsakForUtsettelse) {
        this.periodeFom = periodeFom;
        this.periodeTom = periodeTom;
        this.arsakForUtsettelse = arsakForUtsettelse;
    }
}
