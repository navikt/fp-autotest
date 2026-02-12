package no.nav.foreldrepenger.autotest.klienter.fplos.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

// forenklet utgave av dto fra los
public record LosOppgave(String saksnummer,
                         String fagsakYtelseType,
                         LosReservasjonStatus reservasjonStatus,
                         LocalDateTime opprettetTidspunkt,
                         String personnummer,
                         boolean erTilSaksbehandling,
                         UUID behandlingId,
                         Set<String> andreKriterier) {

    record LosReservasjonStatus(boolean erReservert, String reservertAvUid, String reservertAvNavn) { }

}
