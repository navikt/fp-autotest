package no.nav.foreldrepenger.generator.soknad.maler;

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
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.UttaksplanPeriodeDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.UttakplanPeriodeBuilder;


public final class UttaksperioderMaler {

    private UttaksperioderMaler() {
    }

    public static UttaksplanPeriodeDto uttaksperiode(StønadskontoType stønadskontoType, LocalDate fom, LocalDate tom) {
        return UttakplanPeriodeBuilder.uttak(stønadskontoType, helgejustertTilMandag(fom), helgejustertTilFredag(tom))
                .medErArbeidstaker(true)
                .build();
    }

    public static UttaksplanPeriodeDto uttaksperiode(StønadskontoType stønadskonto, LocalDate fom, LocalDate tom, MorsAktivitet morsAktivitet) {
        return UttakplanPeriodeBuilder.uttak(stønadskonto, helgejustertTilMandag(fom), helgejustertTilFredag(tom))
                .medErArbeidstaker(true)
                .medMorsAktivitetIPerioden(morsAktivitet.name())
                .build();
    }

    public static UttaksplanPeriodeDto uttaksperiode(StønadskontoType stønadskontoType, LocalDate fom, LocalDate tom,
                                              UttaksperiodeType... uttaksperiodeTyper) {
        return uttaksperiode(stønadskontoType, fom, tom, 100, uttaksperiodeTyper);
    }

    public static UttaksplanPeriodeDto uttaksperiode(StønadskontoType stønadskontoType, LocalDate fom, LocalDate tom,
                                              int uttaksprosent, UttaksperiodeType... uttaksperiodeTyper) {
        var periodetype = Set.of(uttaksperiodeTyper);
        return UttakplanPeriodeBuilder.uttak(stønadskontoType, helgejustertTilMandag(fom), helgejustertTilFredag(tom))
                .medSamtidigUttakProsent(Double.valueOf(uttaksprosent))
                .medØnskerFlerbarnsdager(periodetype.contains(UttaksperiodeType.FLERBARNSDAGER))
                .medØnskerSamtidigUttak(periodetype.contains(UttaksperiodeType.SAMTIDIGUTTAK))
                .build();
    }

    public static UttaksplanPeriodeDto graderingsperiodeArbeidstaker(StønadskontoType stønadskontoType,
                                                                     LocalDate fom, LocalDate tom,
                                                                     ArbeidsgiverIdentifikator arbeidsgiverIdentifikator,
                                                                     Integer arbeidstidsprosentIOrgnr) {
        return UttakplanPeriodeBuilder.gradert(stønadskontoType, helgejustertTilMandag(fom), helgejustertTilFredag(tom), Double.valueOf(arbeidstidsprosentIOrgnr))
                .medErArbeidstaker(true)
                .medOrgnumre(List.of(arbeidsgiverIdentifikator.value()))
                .build();
    }

    public static UttaksplanPeriodeDto graderingsperiodeFL(StønadskontoType stønadskontoType,
                                                           LocalDate fom, LocalDate tom,
                                                           Integer arbeidstidsprosent) {
        return UttakplanPeriodeBuilder.gradert(stønadskontoType, helgejustertTilMandag(fom), helgejustertTilFredag(tom), Double.valueOf(arbeidstidsprosent))
                .medErFrilanser(true)
                .build();
    }

    public static UttaksplanPeriodeDto graderingsperiodeSN(StønadskontoType stønadskontoType,
                                                           LocalDate fom, LocalDate tom,
                                                           Integer arbeidstidsprosent) {
        return UttakplanPeriodeBuilder.gradert(stønadskontoType, helgejustertTilMandag(fom), helgejustertTilFredag(tom), Double.valueOf(arbeidstidsprosent))
                .medErSelvstendig(true)
                .build();
    }

    public static UttaksplanPeriodeDto utsettelsesperiode(UtsettelsesÅrsak utsettelseÅrsak, LocalDate fom, LocalDate tom) {
        return UttakplanPeriodeBuilder.utsettelse(utsettelseÅrsak, helgejustertTilMandag(fom), helgejustertTilFredag(tom))
                .medErArbeidstaker(true)
                .build();
    }

    public static UttaksplanPeriodeDto utsettelsesperiode(UtsettelsesÅrsak utsettelseÅrsak, LocalDate fom, LocalDate tom, MorsAktivitet aktivitet) {
        return UttakplanPeriodeBuilder.utsettelse(utsettelseÅrsak, helgejustertTilMandag(fom), helgejustertTilFredag(tom))
                .medMorsAktivitetIPerioden(aktivitet.name())
                .medErArbeidstaker(true)
                .build();
    }

    public static UttaksplanPeriodeDto overføringsperiode(Overføringsårsak overføringÅrsak,
                                                        StønadskontoType stønadskontoType,
                                                        LocalDate fom, LocalDate tom) {
        return UttakplanPeriodeBuilder.overføring(overføringÅrsak, stønadskontoType, helgejustertTilMandag(fom), helgejustertTilFredag(tom))
                .medErArbeidstaker(true)
                .build();
    }

    public static UttaksplanPeriodeDto oppholdsperiode(Oppholdsårsak oppholdsårsak, LocalDate fom, LocalDate tom) {
        return UttakplanPeriodeBuilder.opphold(oppholdsårsak, helgejustertTilMandag(fom), helgejustertTilFredag(tom))
                .medErArbeidstaker(true)
                .build();
    }
}
