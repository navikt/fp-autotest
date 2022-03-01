package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingHenlegg extends BehandlingIdPost {

    protected String årsakKode;
    protected String begrunnelse;

    public BehandlingHenlegg(UUID behandlingUuid, int behandlingVersjon, BehandlingResultatType årsakKode, String begrunnelse) {
        super(behandlingUuid, behandlingVersjon);
        this.årsakKode = årsakKode.name();
        this.begrunnelse = begrunnelse;
    }
}
