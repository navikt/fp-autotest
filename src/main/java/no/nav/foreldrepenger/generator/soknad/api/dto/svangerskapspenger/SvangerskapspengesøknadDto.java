package no.nav.foreldrepenger.generator.soknad.api.dto.svangerskapspenger;

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

public record SvangerskapspengesøknadDto(LocalDate mottattdato,
                                         List<TilretteleggingDto> tilrettelegging,
                                         @Valid @NotNull BarnDto barn,
                                         @Valid @NotNull UtenlandsoppholdDto informasjonOmUtenlandsopphold,
                                         @Valid @NotNull SøkerDto søker,
                                         List<VedleggDto> vedlegg) implements SøknadDto {

    public SvangerskapspengesøknadDto {
        tilrettelegging = Optional.ofNullable(tilrettelegging).orElse(List.of());
        vedlegg = Optional.ofNullable(vedlegg).map(ArrayList::new).orElse(new ArrayList<>());
    }

    @Override
    public Situasjon situasjon() {
        return Situasjon.FØDSEL;
    }
}
