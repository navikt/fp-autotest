package no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RisikovurderingResponse(String risikoklasse, String medlFaresignaler, String iayFaresignaler) {

}
