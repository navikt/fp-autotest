package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class AksjonspunktBehandling {

    @JsonProperty("@type")
    protected String kode;

}
