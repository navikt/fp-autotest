package no.nav.foreldrepenger.generator.soknad.api.erketyper;

import static no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.UttaksperiodeType.OPPHOLD;
import static no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.UttaksperiodeType.OVERFØRING;
import static no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.UttaksperiodeType.UTSETTELSE;
import static no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.UttaksperiodeType.UTTAK;
import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilFredag;
import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilMandag;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.MorsAktivitet;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Oppholdsårsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Overføringsårsak;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.UttaksplanPeriodeDto;
import no.nav.foreldrepenger.generator.soknad.api.dto.ÅpenPeriodeDto;


public final class UttaksperioderErketyper {

    private UttaksperioderErketyper() {
    }

    public static UttaksplanPeriodeDto uttaksperiode(StønadskontoType stønadskontoType, LocalDate fom, LocalDate tom) {
        return new UttaksplanPeriodeDto(
                UTTAK,
                new ÅpenPeriodeDto(helgejustertTilMandag(fom), helgejustertTilFredag(tom)),
                null,
                stønadskontoType,
                null,
                null,
                null,
                null,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null
        );
    }

    public static UttaksplanPeriodeDto uttaksperiode(StønadskontoType stønadskonto, LocalDate fom, LocalDate tom, MorsAktivitet morsAktivitet) {
        return new UttaksplanPeriodeDto(
                UTTAK,
                new ÅpenPeriodeDto(helgejustertTilMandag(fom), helgejustertTilFredag(tom)),
                null,
                stønadskonto,
                morsAktivitet.name(),
                null,
                null,
                null,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null
        );
    }

    public static UttaksplanPeriodeDto uttaksperiode(StønadskontoType stønadskontoType, LocalDate fom, LocalDate tom,
                                              UttaksperiodeType... uttaksperiodeTyper) {
        return uttaksperiode(stønadskontoType, fom, tom, 100, uttaksperiodeTyper);
    }

    public static UttaksplanPeriodeDto uttaksperiode(StønadskontoType stønadskontoType, LocalDate fom, LocalDate tom,
                                              int uttaksprosent, UttaksperiodeType... uttaksperiodeTyper) {
        var periodetype = Set.of(uttaksperiodeTyper);
        return new UttaksplanPeriodeDto(
                UTTAK,
                new ÅpenPeriodeDto(helgejustertTilMandag(fom), helgejustertTilFredag(tom)),
                null,
                stønadskontoType,
                null,
                null,
                Double.valueOf(uttaksprosent),
                null,
                false,
                false,
                false,
                false,
                periodetype.contains(UttaksperiodeType.FLERBARNSDAGER),
                periodetype.contains(UttaksperiodeType.SAMTIDIGUTTAK),
                false,
                null,
                null
        );
    }

    public static UttaksplanPeriodeDto graderingsperiodeArbeidstaker(StønadskontoType stønadskontoType,
                                                                     LocalDate fom, LocalDate tom,
                                                                     ArbeidsgiverIdentifikator arbeidsgiverIdentifikator,
                                                                     Integer arbeidstidsprosentIOrgnr) {
        return new UttaksplanPeriodeDto(
                UTTAK,
                new ÅpenPeriodeDto(helgejustertTilMandag(fom), helgejustertTilFredag(tom)),
                null,
                stønadskontoType,
                null,
                null,
                null,
                Double.valueOf(arbeidstidsprosentIOrgnr),
                true,
                false,
                false,
                true,
                false,
                false,
                false,
                List.of(arbeidsgiverIdentifikator.value()),
                null
        );
    }

    public static UttaksplanPeriodeDto graderingsperiodeFL(StønadskontoType stønadskontoType,
                                                           LocalDate fom, LocalDate tom,
                                                           Integer arbeidstidsprosent) {
        return new UttaksplanPeriodeDto(
                UTTAK,
                new ÅpenPeriodeDto(helgejustertTilMandag(fom), helgejustertTilFredag(tom)),
                null,
                stønadskontoType,
                null,
                null,
                null,
                Double.valueOf(arbeidstidsprosent),
                false,
                true,
                false,
                true,
                false,
                false,
                false,
                null,
                null
        );
    }

    public static UttaksplanPeriodeDto graderingsperiodeSN(StønadskontoType stønadskontoType,
                                                           LocalDate fom, LocalDate tom,
                                                           Integer arbeidstidsprosent) {
        return new UttaksplanPeriodeDto(
                UTTAK,
                new ÅpenPeriodeDto(helgejustertTilMandag(fom), helgejustertTilFredag(tom)),
                null,
                stønadskontoType,
                null,
                null,
                null,
                Double.valueOf(arbeidstidsprosent),
                false,
                false,
                true,
                true,
                false,
                false,
                false,
                null,
                null
        );
    }

    public static UttaksplanPeriodeDto utsettelsesperiode(UtsettelsesÅrsak utsettelseÅrsak, LocalDate fom, LocalDate tom) {
        return new UttaksplanPeriodeDto(
                UTSETTELSE,
                new ÅpenPeriodeDto(helgejustertTilMandag(fom), helgejustertTilFredag(tom)),
                null,
                null,
                null,
                utsettelseÅrsak.name(),
                null,
                null,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null
        );
    }

    public static UttaksplanPeriodeDto utsettelsesperiode(UtsettelsesÅrsak utsettelseÅrsak, LocalDate fom, LocalDate tom, MorsAktivitet aktivitet) {
        return new UttaksplanPeriodeDto(
                UTSETTELSE,
                new ÅpenPeriodeDto(helgejustertTilMandag(fom), helgejustertTilFredag(tom)),
                null,
                null,
                aktivitet.name(),
                utsettelseÅrsak.name(),
                null,
                null,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null
        );
    }

    public static UttaksplanPeriodeDto overføringsperiode(Overføringsårsak overføringÅrsak,
                                                        StønadskontoType stønadskontoType,
                                                        LocalDate fom, LocalDate tom) {
        return new UttaksplanPeriodeDto(
                OVERFØRING,
                new ÅpenPeriodeDto(helgejustertTilMandag(fom), helgejustertTilFredag(tom)),
                null,
                stønadskontoType,
                null,
                overføringÅrsak.name(),
                null,
                null,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null
        );
    }

    public static UttaksplanPeriodeDto oppholdsperiode(Oppholdsårsak oppholdsårsak, LocalDate fom, LocalDate tom) {
        return new UttaksplanPeriodeDto(
                OPPHOLD,
                new ÅpenPeriodeDto(helgejustertTilMandag(fom), helgejustertTilFredag(tom)),
                null,
                null,
                null,
                oppholdsårsak.name(),
                null,
                null,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                null,
                null
        );
    }
}
