package no.nav.foreldrepenger.generator.soknad.api.dto;

import static no.nav.foreldrepenger.common.domain.validation.InputValideringRegex.BARE_BOKSTAVER;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

public record UtenlandsoppholdPeriodeDto(@Pattern(regexp = BARE_BOKSTAVER) String land,
                                         @Valid ÅpenPeriodeDto tidsperiode) {
}
