package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingByttEnhet extends BehandlingIdVersjonDto {

    protected String enhetNavn;
    protected String enhetId;
    protected String begrunnelse;

    public BehandlingByttEnhet(UUID behandlingUuid, int behandlingVersjon, String enhetNavn, String enhetId,
                               String begrunnelse) {
        super(behandlingUuid, behandlingVersjon);
        this.enhetNavn = enhetNavn;
        this.enhetId = enhetId;
        this.begrunnelse = begrunnelse;
    }
}
