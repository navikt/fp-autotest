package no.nav.foreldrepenger.generator.soknad.api.builder;

import no.nav.foreldrepenger.generator.soknad.api.dto.BarnDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.Situasjon;

public record BarnHelper(BarnDto barn, Situasjon situasjon) {
}
