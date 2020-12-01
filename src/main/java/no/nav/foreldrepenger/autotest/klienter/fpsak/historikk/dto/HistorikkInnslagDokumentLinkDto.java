package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record HistorikkInnslagDokumentLinkDto(String tag, URI url, String journalpostId, String dokumentId, boolean utgått) {

}
