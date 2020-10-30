package no.nav.foreldrepenger.autotest.erketyper;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.OverføringÅrsak;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.perioder.GraderingBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.perioder.UttaksperiodeBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Oppholdsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Overfoeringsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Utsettelsesaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Uttaksperiodetyper;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Gradering;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Oppholdsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Overfoeringsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Utsettelsesperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Uttaksperiode;

public class UttaksperioderErketyper {

    public static Uttaksperiode uttaksperiode(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom) {
        return new UttaksperiodeBuilder(stønadskonto.getKode(), fom, tom).build();
    }

    public static Uttaksperiode uttaksperiode(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom,
                                              Boolean flerbarnsdager,
                                              Boolean samtidigUttak) {
        return uttaksperiode(stønadskonto, fom, tom, flerbarnsdager, samtidigUttak, 100);
    }

    public static Uttaksperiode uttaksperiode(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom,
                                              Boolean flerbarnsdager,
                                              Boolean samtidigUttak, int uttaksprosent) {
        UttaksperiodeBuilder uttaksperiodeBuilder = new UttaksperiodeBuilder(stønadskonto.getKode(), fom, tom);
        if (flerbarnsdager) {
            uttaksperiodeBuilder.medFlerbarnsdager();
        }
        if (samtidigUttak) {
            uttaksperiodeBuilder.medSamtidigUttak(BigDecimal.valueOf(uttaksprosent));
        }
        return uttaksperiodeBuilder.build();
    }

    public static Gradering graderingsperiodeArbeidstaker(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom,
                                                          String orgnummer, Integer arbeidstidsprosentIOrgnr) {

        return new GraderingBuilder(stønadskonto.getKode(), fom, tom)
                .medGraderingArbeidstaker(orgnummer, arbeidstidsprosentIOrgnr)
                .build();
    }

    public static Gradering graderingsperiodeFL(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom,
                                                Integer arbeidstidsprosent) {
        return new GraderingBuilder(stønadskonto.getKode(), fom, tom)
                .medGraderingFL(arbeidstidsprosent)
                .build();
    }

    public static Gradering graderingsperiodeSN(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom,
                                                Integer arbeidstidsprosent) {
        return new GraderingBuilder(stønadskonto.getKode(), fom, tom)
                .medGraderingSN(arbeidstidsprosent)
                .build();
    }

    public static Utsettelsesperiode utsettelsesperiode(SøknadUtsettelseÅrsak utsettelseÅrsak, LocalDate fom, LocalDate tom) {
        Utsettelsesperiode utsettelsesperiode = new Utsettelsesperiode();
        utsettelsesperiode.setFom(fom);
        utsettelsesperiode.setTom(tom);
        Utsettelsesaarsaker årsaker = new Utsettelsesaarsaker();
        årsaker.setKode(utsettelseÅrsak.getKode());
        utsettelsesperiode.setAarsak(årsaker);

        return utsettelsesperiode;
    }

    public static Overfoeringsperiode overføringsperiode(OverføringÅrsak overføringÅrsak, Stønadskonto stønadskonto,
                                                         LocalDate fom, LocalDate tom) {
        Overfoeringsaarsaker overfoeringsaarsaker = new Overfoeringsaarsaker();
        overfoeringsaarsaker.setKode(overføringÅrsak.name());

        Uttaksperiodetyper uttaksperiodetyper = new Uttaksperiodetyper();
        uttaksperiodetyper.setKode(stønadskonto.getKode());

        Overfoeringsperiode overfoeringsperiode = new Overfoeringsperiode();
        overfoeringsperiode.setAarsak(overfoeringsaarsaker);
        overfoeringsperiode.setOverfoeringAv(uttaksperiodetyper);
        overfoeringsperiode.setFom(fom);
        overfoeringsperiode.setTom(tom);
        return overfoeringsperiode;
    }

    public static Oppholdsperiode oppholdsperiode(OppholdÅrsak oppholdsårsak, LocalDate fom, LocalDate tom) {
        Oppholdsperiode oppholdsperiode = new Oppholdsperiode();
        Oppholdsaarsaker oppholdsaarsaker = new Oppholdsaarsaker();
        oppholdsaarsaker.setKode(oppholdsårsak.getKode());
        oppholdsperiode.setAarsak(oppholdsaarsaker);
        oppholdsperiode.setFom(fom);
        oppholdsperiode.setTom(tom);
        return oppholdsperiode;
    }
}
