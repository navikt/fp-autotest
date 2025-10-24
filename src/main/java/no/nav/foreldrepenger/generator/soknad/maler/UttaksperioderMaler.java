package no.nav.foreldrepenger.generator.soknad.maler;

import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilFredag;
import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilMandag;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import no.nav.foreldrepenger.kontrakter.fpsoknad.builder.UttakplanPeriodeBuilder;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.MorsAktivitet;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.Oppholdsårsak;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.Overføringsårsak;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.UtsettelsesÅrsak;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.Uttaksplanperiode;


public final class UttaksperioderMaler {

    private UttaksperioderMaler() {
    }

    public static Uttaksplanperiode uttaksperiode(KontoType stønadskontoType, LocalDate fom, LocalDate tom) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.uttak(stønadskontoType, periode.fom, periode.tom).build();
    }

    public static Uttaksplanperiode uttaksperiode(KontoType stønadskonto, LocalDate fom, LocalDate tom, MorsAktivitet morsAktivitet) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.uttak(stønadskonto, periode.fom, periode.tom)
                .medMorsAktivitetIPerioden(morsAktivitet)
                .build();
    }

    public static Uttaksplanperiode uttaksperiode(KontoType stønadskontoType, LocalDate fom, LocalDate tom,
                                              UttaksperiodeType... uttaksperiodeTyper) {
        return uttaksperiode(stønadskontoType, fom, tom, 100, uttaksperiodeTyper);
    }

    public static Uttaksplanperiode uttaksperiode(KontoType stønadskontoType, LocalDate fom, LocalDate tom,
                                              int uttaksprosent, UttaksperiodeType... uttaksperiodeTyper) {
        var periode = justerPeriodeHelg(fom, tom);
        var periodetype = Set.of(uttaksperiodeTyper);
        return UttakplanPeriodeBuilder.uttak(stønadskontoType, periode.fom, periode.tom)
                .medSamtidigUttakProsent(Double.valueOf(uttaksprosent))
                .medØnskerFlerbarnsdager(periodetype.contains(UttaksperiodeType.FLERBARNSDAGER))
                .medØnskerSamtidigUttak(periodetype.contains(UttaksperiodeType.SAMTIDIGUTTAK))
                .build();
    }

    public static Uttaksplanperiode graderingsperiodeArbeidstaker(KontoType stønadskontoType,
                                                                     LocalDate fom,
                                                                     LocalDate tom,
                                                                     String arbeidsgiverIdentifikator,
                                                                     Integer arbeidstidsprosentIOrgnr) {
        return graderingsperiodeArbeidstaker(stønadskontoType, fom, tom, arbeidsgiverIdentifikator, arbeidstidsprosentIOrgnr,
                null);
    }

    public static Uttaksplanperiode graderingsperiodeArbeidstaker(KontoType stønadskontoType,
                                                                     LocalDate fom, LocalDate tom,
                                                                  String arbeidsgiverIdentifikator,
                                                                     Integer arbeidstidsprosentIOrgnr,
                                                                     MorsAktivitet morsAktivitet) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.gradert(stønadskontoType, periode.fom, periode.tom, Double.valueOf(arbeidstidsprosentIOrgnr))
                .medErArbeidstaker(true)
                .medOrgnumre(List.of(arbeidsgiverIdentifikator))
                .medMorsAktivitetIPerioden(morsAktivitet)
                .build();
    }

    public static Uttaksplanperiode graderingsperiodeFL(KontoType stønadskontoType,
                                                           LocalDate fom, LocalDate tom,
                                                           Integer arbeidstidsprosent) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.gradert(stønadskontoType, periode.fom, periode.tom, Double.valueOf(arbeidstidsprosent))
                .medErFrilanser(true)
                .build();
    }

    public static Uttaksplanperiode graderingsperiodeSN(KontoType stønadskontoType,
                                                           LocalDate fom, LocalDate tom,
                                                           Integer arbeidstidsprosent) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.gradert(stønadskontoType, periode.fom, periode.tom, Double.valueOf(arbeidstidsprosent))
                .medErSelvstendig(true)
                .build();
    }

    public static Uttaksplanperiode utsettelsesperiode(UtsettelsesÅrsak utsettelseÅrsak, LocalDate fom, LocalDate tom) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.utsettelse(utsettelseÅrsak, periode.fom, periode.tom)
                .medErArbeidstaker(true)
                .build();
    }



    public static Uttaksplanperiode utsettelsesperiode(UtsettelsesÅrsak utsettelseÅrsak, LocalDate fom, LocalDate tom, MorsAktivitet aktivitet) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.utsettelse(utsettelseÅrsak, periode.fom, periode.tom)
                .medMorsAktivitetIPerioden(aktivitet)
                .medErArbeidstaker(true)
                .build();
    }

    public static Uttaksplanperiode overføringsperiode(Overføringsårsak overføringÅrsak,
                                                       KontoType stønadskontoType,
                                                        LocalDate fom, LocalDate tom) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.overføring(overføringÅrsak, stønadskontoType, periode.fom, periode.tom)
                .build();
    }

    public static Uttaksplanperiode oppholdsperiode(Oppholdsårsak oppholdsårsak, LocalDate fom, LocalDate tom) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakplanPeriodeBuilder.opphold(oppholdsårsak, periode.fom, periode.tom)
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
