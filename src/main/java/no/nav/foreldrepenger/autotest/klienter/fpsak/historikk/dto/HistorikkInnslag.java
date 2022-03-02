package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HistorikkInnslag(UUID behandlingUuid,
                               HistorikkinnslagType type,
                               String aktoer,
                               String kjoenn,
                               List<HistorikkInnslagDokumentLinkDto> dokumentLinks,
                               List<HistorikkinnslagDel> historikkinnslagDeler) {
}
