package no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record BehandlendeEnhet(String enhetId, String enhetNavn, String status) {

}
