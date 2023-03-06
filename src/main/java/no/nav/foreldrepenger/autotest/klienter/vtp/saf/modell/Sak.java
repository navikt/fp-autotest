package no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Sak(@JsonProperty("arkivsaksnummer") String arkivsaksnummer,
                  @JsonProperty("fagsakId") String fagsakId,
                  @JsonProperty("fagsaksystem") String fagsaksystem) {

}
