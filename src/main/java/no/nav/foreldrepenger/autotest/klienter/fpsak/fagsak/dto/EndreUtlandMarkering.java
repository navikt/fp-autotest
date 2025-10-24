package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto;

import no.nav.foreldrepenger.kontrakter.fpsoknad.Saksnummer;

import java.util.Set;

public record EndreUtlandMarkering(Saksnummer saksnummer, Set<String> fagsakMarkeringer) {
}
