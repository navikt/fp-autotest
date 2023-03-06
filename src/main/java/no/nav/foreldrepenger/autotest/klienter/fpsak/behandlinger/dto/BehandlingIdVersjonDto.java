package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;

public class BehandlingIdVersjonDto extends BehandlingIdDto {

    @NotNull
    private final int behandlingVersjon;

    @JsonCreator
    public BehandlingIdVersjonDto(UUID behandlingUuid, int behandlingVersjon) {
        super(behandlingUuid);
        this.behandlingVersjon = behandlingVersjon;
    }

    public BehandlingIdVersjonDto(Behandling behandling) {
        this(behandling.uuid, behandling.versjon);
    }

    public int behandlingVersjon() {
        return behandlingVersjon;
    }

}
