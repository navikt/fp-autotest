package no.nav.foreldrepenger.autotest.klienter.vtp.journalpost.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JournalpostIdDto(String journalpostId) {

}
