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
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.uttak(stønadskontoType, periode.fom, periode.tom)
                .medErArbeidstaker(true)
                .build();
    }

    public static UttaksplanPeriodeDto uttaksperiode(StønadskontoType stønadskonto, LocalDate fom, LocalDate tom, MorsAktivitet morsAktivitet) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.uttak(stønadskonto, periode.fom, periode.tom)
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
        var periode = justerPeriodeHelg(fom, tom);
        var periodetype = Set.of(uttaksperiodeTyper);
        return UttakplanPeriodeBuilder.uttak(stønadskontoType, periode.fom, periode.tom)
                .medSamtidigUttakProsent(Double.valueOf(uttaksprosent))
                .medØnskerFlerbarnsdager(periodetype.contains(UttaksperiodeType.FLERBARNSDAGER))
                .medØnskerSamtidigUttak(periodetype.contains(UttaksperiodeType.SAMTIDIGUTTAK))
                .build();
    }

    public static UttaksplanPeriodeDto graderingsperiodeArbeidstaker(StønadskontoType stønadskontoType,
                                                                     LocalDate fom,
                                                                     LocalDate tom,
                                                                     ArbeidsgiverIdentifikator arbeidsgiverIdentifikator,
                                                                     Integer arbeidstidsprosentIOrgnr) {
        return graderingsperiodeArbeidstaker(stønadskontoType, fom, tom, arbeidsgiverIdentifikator, arbeidstidsprosentIOrgnr,
                null);
    }

    public static UttaksplanPeriodeDto graderingsperiodeArbeidstaker(StønadskontoType stønadskontoType,
                                                                     LocalDate fom, LocalDate tom,
                                                                     ArbeidsgiverIdentifikator arbeidsgiverIdentifikator,
                                                                     Integer arbeidstidsprosentIOrgnr,
                                                                     MorsAktivitet morsAktivitet) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.gradert(stønadskontoType, periode.fom, periode.tom, Double.valueOf(arbeidstidsprosentIOrgnr))
                .medErArbeidstaker(true)
                .medOrgnumre(List.of(arbeidsgiverIdentifikator.value()))
                .medMorsAktivitetIPerioden(morsAktivitet == null ? null : morsAktivitet.name())
                .build();
    }

    public static UttaksplanPeriodeDto graderingsperiodeFL(StønadskontoType stønadskontoType,
                                                           LocalDate fom, LocalDate tom,
                                                           Integer arbeidstidsprosent) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.gradert(stønadskontoType, periode.fom, periode.tom, Double.valueOf(arbeidstidsprosent))
                .medErFrilanser(true)
                .build();
    }

    public static UttaksplanPeriodeDto graderingsperiodeSN(StønadskontoType stønadskontoType,
                                                           LocalDate fom, LocalDate tom,
                                                           Integer arbeidstidsprosent) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.gradert(stønadskontoType, periode.fom, periode.tom, Double.valueOf(arbeidstidsprosent))
                .medErSelvstendig(true)
                .build();
    }

    public static UttaksplanPeriodeDto utsettelsesperiode(UtsettelsesÅrsak utsettelseÅrsak, LocalDate fom, LocalDate tom) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.utsettelse(utsettelseÅrsak, periode.fom, periode.tom)
                .medErArbeidstaker(true)
                .build();
    }



    public static UttaksplanPeriodeDto utsettelsesperiode(UtsettelsesÅrsak utsettelseÅrsak, LocalDate fom, LocalDate tom, MorsAktivitet aktivitet) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.utsettelse(utsettelseÅrsak, periode.fom, periode.tom)
                .medMorsAktivitetIPerioden(aktivitet.name())
                .medErArbeidstaker(true)
                .build();
    }

    public static UttaksplanPeriodeDto overføringsperiode(Overføringsårsak overføringÅrsak,
                                                        StønadskontoType stønadskontoType,
                                                        LocalDate fom, LocalDate tom) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.overføring(overføringÅrsak, stønadskontoType, periode.fom, periode.tom)
                .medErArbeidstaker(true)
                .build();
    }

    public static UttaksplanPeriodeDto oppholdsperiode(Oppholdsårsak oppholdsårsak, LocalDate fom, LocalDate tom) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.opphold(oppholdsårsak, periode.fom, periode.tom)
                .medErArbeidstaker(true)
                .build();
    }

    private static Periode justerPeriodeHelg(LocalDate fom, LocalDate tom) {
        if (fom.plusDays(1).equals(tom)) {
            return new Periode(helgejustertTilFredag(fom), helgejustertTilMandag(tom));
        } else {
            return new Periode(helgejustertTilMandag(fom), helgejustertTilFredag(tom));
        }
    }

    public record Periode(LocalDate fom, LocalDate tom) {
    }
}
