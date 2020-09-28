package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BehandlingIdBasicDto {
    protected int behandlingId;
    protected UUID uuid;

    public BehandlingIdBasicDto(int behandlingId) {
        this.behandlingId = behandlingId;
    }

    public BehandlingIdBasicDto(UUID uuid) {
        this.uuid = uuid;
    }
}
