package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.common.domain.Saksnummer;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Fagsak(Saksnummer saksnummer, FagsakStatus status, LocalDate barnFodt) {

}
