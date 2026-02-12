package no.nav.foreldrepenger.autotest.klienter.fplos.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record SakslisteIdDto(@NotNull @Digits(integer = 18, fraction = 0) Long sakslisteId) {
}
