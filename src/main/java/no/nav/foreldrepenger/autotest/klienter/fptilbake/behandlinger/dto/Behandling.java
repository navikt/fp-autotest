package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Behandling {

    public int id;
    public UUID uuid;
    public int fagsakId;
}
