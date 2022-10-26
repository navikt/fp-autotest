package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.time.LocalDate;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Venteårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;

public class SettBehandlingPaVentDto extends BehandlingIdVersjonDto {

    private final LocalDate frist;
    private final Venteårsak ventearsak;

    public SettBehandlingPaVentDto(UUID behandlingUuid, int behandlingVersjon, LocalDate frist, Venteårsak ventearsak) {
        super(behandlingUuid, behandlingVersjon);
        this.frist = frist;
        this.ventearsak = ventearsak;
    }

    public SettBehandlingPaVentDto(Behandling behandling, LocalDate frist, Venteårsak ventearsak) {
        this(behandling.uuid, behandling.versjon, frist, ventearsak);
    }

    public LocalDate frist() {
        return frist;
    }

    public Venteårsak ventearsak() {
        return ventearsak;
    }
}
