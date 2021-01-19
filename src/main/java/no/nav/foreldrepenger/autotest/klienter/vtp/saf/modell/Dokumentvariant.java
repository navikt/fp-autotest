package no.nav.foreldrepenger.autotest.klienter.vtp.saf.modell;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Dokumentvariant(VariantFormat variantformat) {

}
