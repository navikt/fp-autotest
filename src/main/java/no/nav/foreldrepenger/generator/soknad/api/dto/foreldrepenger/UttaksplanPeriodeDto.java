package no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger;

import static java.util.Collections.emptyList;
import static no.nav.foreldrepenger.common.domain.validation.InputValideringRegex.BARE_BOKSTAVER;

import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.generator.soknad.api.dto.MutableVedleggReferanseDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.ÅpenPeriodeDto;

// TODO: Rydd opp
public record UttaksplanPeriodeDto(@NotNull UttaksperiodeType type,
                                   @Valid ÅpenPeriodeDto tidsperiode,
                                   @Pattern(regexp = BARE_BOKSTAVER) String forelder,
                                   StønadskontoType konto,
                                   @Pattern(regexp = "^[\\p{Digit}\\p{L}_]*$") String morsAktivitetIPerioden,
                                   @Pattern(regexp = "^[\\p{Digit}\\p{L}_]*$") String årsak,
                                   Double samtidigUttakProsent,
                                   Double stillingsprosent,
                                   boolean erArbeidstaker,
                                   boolean erFrilanser,
                                   boolean erSelvstendig,
                                   boolean gradert,
                                   boolean ønskerFlerbarnsdager,
                                   boolean ønskerSamtidigUttak,
                                   Boolean justeresVedFødsel,
                                   List<String> orgnumre,
                                   List<MutableVedleggReferanseDto> vedlegg) {

    public UttaksplanPeriodeDto {
        orgnumre = Optional.ofNullable(orgnumre).orElse(emptyList());
        vedlegg = Optional.ofNullable(vedlegg).orElse(emptyList());
    }

    public enum Type {

    }
}
