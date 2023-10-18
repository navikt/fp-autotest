package no.nav.foreldrepenger.generator.soknad.api.dto.engangsstønad;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import no.nav.foreldrepenger.generator.soknad.api.dto.BarnDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøkerDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøknadDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.UtenlandsoppholdDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.VedleggDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.Situasjon;
import no.nav.foreldrepenger.generator.soknad.api.dto.validering.VedlegglistestørrelseConstraint;

public record EngangsstønadDto(LocalDate mottattdato,
                               @Valid @NotNull BarnDto barn,
                               @Valid @NotNull UtenlandsoppholdDto informasjonOmUtenlandsopphold,
                               @Valid @NotNull SøkerDto søker,
                               @Valid @VedlegglistestørrelseConstraint List<VedleggDto> vedlegg) implements SøknadDto {

    public EngangsstønadDto {
       vedlegg = Optional.ofNullable(vedlegg).map(ArrayList::new).orElse(new ArrayList<>());
    }

    @Override
    public Situasjon situasjon() {
        if (barn.adopsjonsdato() != null) {
            return Situasjon.ADOPSJON;
        } else {
            return Situasjon.FØDSEL;
        }
    }
}
