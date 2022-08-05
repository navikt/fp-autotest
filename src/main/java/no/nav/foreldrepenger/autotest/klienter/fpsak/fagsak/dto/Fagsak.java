package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.Saksnummer;

public record Fagsak(Saksnummer saksnummer, FagsakStatus status, LocalDate barnFodt) {

}
