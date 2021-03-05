package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingHenlegg extends BehandlingIdPost {

    protected String årsakKode;
    protected String begrunnelse;

    public BehandlingHenlegg(int behandlingId, int behandlingVersjon, BehandlingResultatType årsakKode, String begrunnelse) {
        super(behandlingId, behandlingVersjon);
        this.årsakKode = årsakKode.getKode();
        this.begrunnelse = begrunnelse;
    }
}
