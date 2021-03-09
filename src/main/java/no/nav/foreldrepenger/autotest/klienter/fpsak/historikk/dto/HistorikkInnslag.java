package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HistorikkInnslag(int behandlingId,
                               HistorikkinnslagType type,
                               Kode aktoer,
                               Kode kjoenn,
                               List<HistorikkInnslagDokumentLinkDto> dokumentLinks,
                               List<HistorikkinnslagDel> historikkinnslagDeler) {
}
