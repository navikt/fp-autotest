package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.NONE, setterVisibility= JsonAutoDetect.Visibility.NONE, fieldVisibility= JsonAutoDetect.Visibility.ANY)
public class BehandlingIdDto {

    protected UUID behandlingUuid;

    public BehandlingIdDto(UUID behandlingUuid) {
        this.behandlingUuid = behandlingUuid;
    }

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

}
