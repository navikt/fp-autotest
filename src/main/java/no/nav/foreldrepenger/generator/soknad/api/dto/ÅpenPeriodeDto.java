package no.nav.foreldrepenger.generator.soknad.api.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record ÅpenPeriodeDto(@NotNull LocalDate fom, LocalDate tom) {
}
