package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Fagsak(Saksnummer saksnummer, FagsakStatus status, LocalDate barnFodt) {

}
