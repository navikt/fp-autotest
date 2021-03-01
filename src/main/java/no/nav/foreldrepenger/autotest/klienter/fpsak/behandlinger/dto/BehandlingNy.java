package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingNy {
    protected Long saksnummer;
    protected BehandlingType behandlingType;
    protected BehandlingÅrsakType behandlingArsakType;
    protected Boolean nyBehandlingEtterKlage = null;

    public BehandlingNy(Long saksnummer, BehandlingType behandlingType, BehandlingÅrsakType behandlingArsakType) {
        super();
        this.saksnummer = saksnummer;
        this.behandlingType = behandlingType;
        this.behandlingArsakType = behandlingArsakType;
    }
}
