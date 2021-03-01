package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.FagsakStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Fagsak(Long saksnummer, FagsakStatus status) {

}
