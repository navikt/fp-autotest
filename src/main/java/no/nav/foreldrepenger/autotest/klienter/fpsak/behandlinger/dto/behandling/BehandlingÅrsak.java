package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingÅrsak {

    protected BehandlingÅrsakType behandlingArsakType;
    protected boolean manueltOpprettet;

    public BehandlingÅrsakType getBehandlingArsakType() {
        return behandlingArsakType;
    }

    public boolean getManueltOpprettet() {
        return manueltOpprettet;
    }
}
