package no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JournalpostIdDto(@JsonProperty("journalpostId") String journalpostId) {

}
