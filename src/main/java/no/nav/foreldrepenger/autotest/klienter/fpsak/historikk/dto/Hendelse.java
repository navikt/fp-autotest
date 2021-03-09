package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Hendelse(Kode navn) {

}
