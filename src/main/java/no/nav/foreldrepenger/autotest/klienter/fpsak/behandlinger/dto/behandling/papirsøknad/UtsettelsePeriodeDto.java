package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;

public class UtsettelsePeriodeDto {

    public final LocalDate periodeFom;
    public final LocalDate periodeTom;
    public final SøknadUtsettelseÅrsak arsakForUtsettelse;

    public UtsettelsePeriodeDto(LocalDate periodeFom, LocalDate periodeTom, SøknadUtsettelseÅrsak arsakForUtsettelse) {
        this.periodeFom = periodeFom;
        this.periodeTom = periodeTom;
        this.arsakForUtsettelse = arsakForUtsettelse;
    }
}
