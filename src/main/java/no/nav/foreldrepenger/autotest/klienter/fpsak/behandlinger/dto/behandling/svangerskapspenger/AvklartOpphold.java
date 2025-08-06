package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AvklartOpphold(@NotNull LocalDate fom, @NotNull LocalDate tom, @NotNull String oppholdÅrsak,
                             @NotNull SvpOppholdKilde oppholdKilde, boolean forVisning) {

    public enum SvpOppholdKilde {
        SØKNAD,
        INNTEKTSMELDING,
        REGISTRERT_AV_SAKSBEHANDLER
    }
}
