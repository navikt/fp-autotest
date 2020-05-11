package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingÅrsak {

    protected Kode behandlingArsakType;
    protected boolean manueltOpprettet;

    public Kode getBehandlingArsakType() {
        return behandlingArsakType;
    }

    public boolean getManueltOpprettet() {
        return manueltOpprettet;
    }
}
