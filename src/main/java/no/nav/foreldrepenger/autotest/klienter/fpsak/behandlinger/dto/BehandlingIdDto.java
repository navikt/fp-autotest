package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

public class BehandlingIdDto {

    @NotNull
    private final UUID behandlingUuid;

    public BehandlingIdDto(UUID behandlingUuid) {
        this.behandlingUuid = behandlingUuid;
    }

    public UUID behandlingUuid() {
        return behandlingUuid;
    }

}
