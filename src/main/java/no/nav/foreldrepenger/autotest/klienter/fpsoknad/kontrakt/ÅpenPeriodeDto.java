package no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record ÅpenPeriodeDto(@NotNull LocalDate fom, LocalDate tom) {
}
