package no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger;

import static no.nav.foreldrepenger.common.domain.validation.InputValideringRegex.FRITEKST;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import no.nav.foreldrepenger.generator.soknad.api.dto.BarnDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøkerDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøknadDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.UtenlandsoppholdDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.VedleggDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.validering.VedlegglistestørrelseConstraint;

public record ForeldrepengesøknadDto(LocalDate mottattdato,
                                     @NotNull Situasjon situasjon,
                                     @Valid @NotNull SøkerDto søker,
                                     @Valid @NotNull BarnDto barn,
                                     @Valid @NotNull AnnenforelderDto annenForelder,
                                     @Valid @NotNull Dekningsgrad dekningsgrad,
                                     @Pattern(regexp = FRITEKST) String tilleggsopplysninger,
                                     @Valid @NotNull UtenlandsoppholdDto informasjonOmUtenlandsopphold,
                                     @Valid List<UttaksplanPeriodeDto> uttaksplan,
                                     Boolean ønskerJustertUttakVedFødsel,
                                     @Valid @VedlegglistestørrelseConstraint List<VedleggDto> vedlegg) implements SøknadDto {
    public ForeldrepengesøknadDto {
        uttaksplan = Optional.ofNullable(uttaksplan).orElse(List.of());
        vedlegg = Optional.ofNullable(vedlegg).map(ArrayList::new).orElse(new ArrayList<>());
    }
}
