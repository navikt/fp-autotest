package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Behandling {

    public int id;
    public UUID uuid;
    public int versjon;
    public int fagsakId;
    public BehandlingType type;
    public BehandlingStatus status;
    public boolean behandlingPaaVent;
    public String venteArsakKode;
    public LocalDateTime avsluttet;
    public LocalDateTime opprettet;
    protected boolean harVerge;

    public boolean harVerge() {
        return harVerge;
    }
}
