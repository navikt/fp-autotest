package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.common.domain.Saksnummer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingNy {
    protected Saksnummer saksnummer;
    protected BehandlingType behandlingType;
    protected BehandlingÅrsakType behandlingArsakType;

    public BehandlingNy(Saksnummer saksnummer, BehandlingType behandlingType, BehandlingÅrsakType behandlingArsakType) {
        super();
        this.saksnummer = saksnummer;
        this.behandlingType = behandlingType;
        this.behandlingArsakType = behandlingArsakType;
    }
}
