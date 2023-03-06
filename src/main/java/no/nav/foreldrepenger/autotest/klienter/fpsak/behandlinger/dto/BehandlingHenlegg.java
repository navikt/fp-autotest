package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.UUID;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;

public final class BehandlingHenlegg extends BehandlingIdVersjonDto {

    private final String årsakKode;
    private final String begrunnelse;

    public BehandlingHenlegg(UUID behandlingUuid, int behandlingVersjon, BehandlingResultatType årsakKode, String begrunnelse) {
        super(behandlingUuid, behandlingVersjon);
        this.årsakKode = årsakKode.name();
        this.begrunnelse = begrunnelse;
    }

    public String årsakKode() {
        return årsakKode;
    }

    public String begrunnelse() {
        return begrunnelse;
    }
}
