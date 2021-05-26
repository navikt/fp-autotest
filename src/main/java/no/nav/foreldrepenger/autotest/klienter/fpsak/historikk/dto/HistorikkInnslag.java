package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HistorikkInnslag(UUID behandlingUuid,
                               HistorikkinnslagType type,
                               Kode aktoer,
                               Kode kjoenn,
                               List<HistorikkInnslagDokumentLinkDto> dokumentLinks,
                               List<HistorikkinnslagDel> historikkinnslagDeler) {
}
