package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.util.List;
import java.util.UUID;

public record HistorikkInnslag(
        UUID behandlingUuid,
        HistorikkinnslagType type,
        String aktoer,
        String kjoenn,
        List<HistorikkInnslagDokumentLinkDto> dokumentLinks) {
}
