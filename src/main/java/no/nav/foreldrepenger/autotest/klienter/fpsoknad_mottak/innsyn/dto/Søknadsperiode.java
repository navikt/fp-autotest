package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto;

import java.time.LocalDate;

record Søknadsperiode(LocalDate fom, LocalDate tom, KontoType kontoType) {

}
