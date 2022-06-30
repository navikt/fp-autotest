package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Fagsak(String saksnummer, FagsakStatus status) {

}
