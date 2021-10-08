package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpprettSak(String journalpostId, String behandlingstemaOffisiellKode, String aktørId) {
//TODO AktørID
}
