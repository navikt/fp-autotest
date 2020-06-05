package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Behandling {

    public int id;
    public UUID uuid;
    public int versjon;
    public int fagsakId;
    public BehandlingType type;
    public boolean behandlingPaaVent;
    public String venteArsakKode;
    public BehandlingType status;
    protected boolean harVerge;

    public boolean harVerge() {
        return harVerge;
    }
}
