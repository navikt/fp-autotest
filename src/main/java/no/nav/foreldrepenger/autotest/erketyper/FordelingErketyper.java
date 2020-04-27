package no.nav.foreldrepenger.autotest.erketyper;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.OverføringÅrsak;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.perioder.GraderingBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.perioder.UttaksperiodeBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Oppholdsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Overfoeringsaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Utsettelsesaarsaker;
import no.nav.vedtak.felles.xml.soeknad.kodeverk.v3.Uttaksperiodetyper;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Gradering;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Oppholdsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Overfoeringsperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Utsettelsesperiode;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Uttaksperiode;

import java.math.BigDecimal;
import java.time.LocalDate;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FEDREKVOTE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;

public class FordelingErketyper {

    public static Fordeling fordelingHappyCase(LocalDate familehendelseDato, SøkersRolle søkerRolle){
        if(søkerRolle == SøkersRolle.MOR){
            return fordelingMorHappyCaseLong(familehendelseDato);
        }
        else {
            return fordelingFarHappyCase(familehendelseDato);
        }
    }
    public static Fordeling fordelingMorHappyCase(LocalDate familehendelseDato) {
        return generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(3), familehendelseDato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, familehendelseDato, familehendelseDato.plusWeeks(10)));
    }

    public static Fordeling fordelingMorHappyCaseLong(LocalDate familehendelseDato) {
        return generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(3), familehendelseDato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, familehendelseDato, familehendelseDato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, familehendelseDato.plusWeeks(15), familehendelseDato.plusWeeks(31).minusDays(1)));
    }

    public static Fordeling fordelingMorHappyCaseEkstraUttakFørFødsel(LocalDate familehendelseDato) {
        return generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(12), familehendelseDato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, familehendelseDato, familehendelseDato.plusWeeks(10)));
    }

    public static Fordeling fordelingMorHappyCaseMedEkstraUttak(LocalDate familiehendelseDato) {
        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        fordeling.getPerioder().add(uttaksperiode(FELLESPERIODE, familiehendelseDato.plusWeeks(10).plusDays(1), familiehendelseDato.plusWeeks(12)));
        return fordeling;
    }

    public static Fordeling fordelingMorMedAksjonspunkt(LocalDate familiehendelseDato) {
        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        fordeling.getPerioder().add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, familiehendelseDato.minusWeeks(3), familiehendelseDato.minusDays(1)));
        return fordeling;
    }

    public static Gradering graderingsperiodeArbeidstaker(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom, String orgnummer, Integer arbeidstidsprosentIOrgnr) {
        return new GraderingBuilder(stønadskonto.getKode(), fom, tom)
                .medGraderingArbeidstaker(orgnummer, arbeidstidsprosentIOrgnr)
                .build();
    }
    public static Gradering graderingsperiodeFL(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom, Integer arbeidstidsprosent) {
        return new GraderingBuilder(stønadskonto.getKode(), fom, tom)
                .medGraderingFL(arbeidstidsprosent)
                .build();
    }
    public static Gradering graderingsperiodeSN(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom, Integer arbeidstidsprosent) {
        return new GraderingBuilder(stønadskonto.getKode(), fom, tom)
                .medGraderingSN(arbeidstidsprosent)
                .build();
    }

    public static Utsettelsesperiode utsettelsesperiode(SøknadUtsettelseÅrsak årsak, LocalDate fom, LocalDate tom) {
        Utsettelsesperiode utsettelsesperiode = new Utsettelsesperiode();
        utsettelsesperiode.setFom(fom);
        utsettelsesperiode.setTom(tom);
        Utsettelsesaarsaker årsaker = new Utsettelsesaarsaker();
        årsaker.setKode(årsak.getKode());
        utsettelsesperiode.setAarsak(årsaker);

        return utsettelsesperiode;
    }
    //TODO Kan slettes
    public static Uttaksperiode uttaksperiode(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom) {
        return new UttaksperiodeBuilder(stønadskonto.getKode(), fom, tom).build();
    }
    public static Uttaksperiode uttaksperiode(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom, Boolean flerbarnsdager,
                                              Boolean samtidigUttak) {
        return uttaksperiode(stønadskonto, fom, tom, flerbarnsdager, samtidigUttak, 100);
    }
    public static Uttaksperiode uttaksperiode(Stønadskonto stønadskonto, LocalDate fom, LocalDate tom, Boolean flerbarnsdager,
                                              Boolean samtidigUttak,  int uttaksprosent) {
        UttaksperiodeBuilder uttaksperiodeBuilder = new UttaksperiodeBuilder(stønadskonto.getKode(), fom, tom);
        if ( flerbarnsdager ) {
            uttaksperiodeBuilder.medFlerbarnsdager();
        }
        if ( samtidigUttak ) {
            uttaksperiodeBuilder.medSamtidigUttak(BigDecimal.valueOf(uttaksprosent));
        }
        return  uttaksperiodeBuilder.build();
    }


    public static Overfoeringsperiode overføringsperiode(OverføringÅrsak overføringÅrsak, Stønadskonto stønadskonto, LocalDate fom, LocalDate tom) {
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
        addPeriode(fom, tom, oppholdsperiode);
        return oppholdsperiode;

    }

    public static void addPeriode(LocalDate fom, LocalDate tom, LukketPeriodeMedVedlegg uttaksperiode) {
        uttaksperiode.setFom((fom));
        uttaksperiode.setTom((tom));
    }

    public static void addStønadskontotype(Stønadskonto stønadskontotype, Uttaksperiode uttaksperiode) {
        Uttaksperiodetyper uttaksperiodetyper = new Uttaksperiodetyper();
        uttaksperiodetyper.setKode(stønadskontotype.getKode());
        uttaksperiode.setType(uttaksperiodetyper);
    }

    public static Fordeling fordelingFarHappyCase(LocalDate familehendelseDato) {
        return generiskFordeling(uttaksperiode(FELLESPERIODE, familehendelseDato.plusWeeks(3), familehendelseDato.plusWeeks(5)));
    }
    public static Fordeling fordelingFarHappyCase() {
        return generiskFordeling(uttaksperiode(FEDREKVOTE, LocalDate.now().plusWeeks(3), LocalDate.now().plusWeeks(18).minusDays(1)));
    }

    public static Fordeling fordelingFarHappyCaseMedMor(LocalDate familiehendelseDato) {
        return generiskFordeling(uttaksperiode(FELLESPERIODE, familiehendelseDato.plusWeeks(6), familiehendelseDato.plusWeeks(9)));
    }

    public static Fordeling fordelingFarUtenOverlapp(LocalDate familehendelseDato) {
        return generiskFordeling(uttaksperiode(FELLESPERIODE, familehendelseDato.plusWeeks(6).plusDays(1), familehendelseDato.plusWeeks(8)));
    }

    public static Fordeling fordelingFarHappycaseKobletMedMorHappycase(LocalDate familehendelseDato) {
        return generiskFordeling(uttaksperiode(FEDREKVOTE, familehendelseDato.plusWeeks(10).plusDays(1), familehendelseDato.plusWeeks(16)));
    }

    //TODO Flytte til TestBase
    public static Fordeling generiskFordeling(LukketPeriodeMedVedlegg... perioder) {
        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);

        for (LukketPeriodeMedVedlegg lukketPeriodeMedVedlegg : perioder) {
            fordeling.getPerioder().add(lukketPeriodeMedVedlegg);
        }

        return fordeling;
    }

    public static Fordeling fordelingMorAleneomsorgHappyCase(LocalDate familehendelseDato) {
        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(false);

        fordeling.getPerioder().add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(3), familehendelseDato.minusDays(1)));
        fordeling.getPerioder().add(uttaksperiode(FORELDREPENGER, familehendelseDato, familehendelseDato.plusWeeks(100)));

        return fordeling;
    }

    public static Fordeling fordelingFarAleneomsorg(LocalDate familehendelseDato) {
        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(false);

        fordeling.getPerioder().add(uttaksperiode(FORELDREPENGER, familehendelseDato, familehendelseDato.plusWeeks(20)));

        return fordeling;
    }
}
