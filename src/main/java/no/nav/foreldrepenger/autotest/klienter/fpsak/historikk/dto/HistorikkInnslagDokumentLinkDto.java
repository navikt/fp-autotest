package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HistorikkInnslagDokumentLinkDto(String tag, URI url, String journalpostId, String dokumentId, boolean utgått) {

}
