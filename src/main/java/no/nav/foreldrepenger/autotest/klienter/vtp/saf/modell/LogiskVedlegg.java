package no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LogiskVedlegg(@JsonProperty("filnavn") String tittel) {

}
