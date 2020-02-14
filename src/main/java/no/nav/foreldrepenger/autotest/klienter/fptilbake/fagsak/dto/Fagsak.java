package no.nav.foreldrepenger.autotest.klienter.fptilbake.fagsak.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Fagsak {
    public long saksnummer;
}
