package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import java.util.UUID;

public class BehandlingIdBasicDto {
    protected int behandlingId;
    protected UUID uuid;

    public BehandlingIdBasicDto(int behandlingId) {
        this.behandlingId = behandlingId;
    }

}
