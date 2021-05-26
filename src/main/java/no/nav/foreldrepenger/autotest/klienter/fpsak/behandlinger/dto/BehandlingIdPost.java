package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingIdPost {

    protected UUID behandlingUuid;
    protected int behandlingVersjon;

    public BehandlingIdPost(UUID behandlingUuid, int behandlingVersjon) {
        super();
        this.behandlingUuid = behandlingUuid;
        this.behandlingVersjon = behandlingVersjon;
    }

    public BehandlingIdPost(Behandling behandling) {
        this(behandling.uuid, behandling.versjon);
    }
}
