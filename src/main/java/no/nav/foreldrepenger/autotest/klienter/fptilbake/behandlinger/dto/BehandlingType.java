package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties (ignoreUnknown = true)
public class BehandlingType {

    public String kode;
    protected String kodeverk;

    public BehandlingType() {
        this.kodeverk = "FAGSAK_YTELSE";
    }
}
