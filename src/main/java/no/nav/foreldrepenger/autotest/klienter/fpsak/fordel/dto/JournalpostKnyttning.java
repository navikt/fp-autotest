package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JournalpostKnyttning(Saksnummer saksnummerDto, JournalpostId journalpostIdDto) {

}
