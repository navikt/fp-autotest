package no.nav.foreldrepenger.generator.soknad.api.dto.endringssøknad;

import static no.nav.foreldrepenger.common.domain.validation.InputValideringRegex.FRITEKST;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.generator.soknad.api.dto.BarnDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøkerDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.VedleggDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.AnnenforelderDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.Situasjon;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.UttaksplanPeriodeDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.validering.VedlegglistestørrelseConstraint;

public record EndringssøknadForeldrepengerDto(LocalDate mottattdato,
                                              @NotNull Situasjon situasjon,
                                              @Valid @NotNull Saksnummer saksnummer,
                                              @Valid @NotNull SøkerDto søker,
                                              @Valid @NotNull BarnDto barn,
                                              @Valid @NotNull AnnenforelderDto annenforelder,
                                              @Pattern(regexp = FRITEKST) String tilleggsopplysninger,
                                              Boolean ønskerJustertUttakVedFødsel,
                                              @Valid @Size(max = 100) List<UttaksplanPeriodeDto> uttaksplan,
                                              @Valid @VedlegglistestørrelseConstraint List<VedleggDto> vedlegg) implements EndringssøknadDto {
}
