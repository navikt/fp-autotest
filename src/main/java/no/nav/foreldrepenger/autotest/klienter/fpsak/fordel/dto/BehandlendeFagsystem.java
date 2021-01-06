package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BehandlendeFagsystem(boolean behandlesIVedtaksløsningen, boolean sjekkMotInfotrygd,
                                   boolean manuellVurdering, Saksnummer saksnummer) {

}
