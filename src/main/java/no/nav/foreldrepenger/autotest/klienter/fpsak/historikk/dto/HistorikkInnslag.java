package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record HistorikkInnslag(
        UUID behandlingUuid,
        HistorikkinnslagType type,
        HistorikkAktør aktoer,
        String kjoenn,
        LocalDateTime opprettetTidspunkt,
        List<HistorikkInnslagDokumentLinkDto> dokumentLinks) {
}
