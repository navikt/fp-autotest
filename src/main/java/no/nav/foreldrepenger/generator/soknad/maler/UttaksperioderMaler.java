package no.nav.foreldrepenger.generator.soknad.maler;

import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilFredag;
import static no.nav.foreldrepenger.generator.soknad.util.VirkedagUtil.helgejustertTilMandag;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;
import no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.MorsAktivitet;
import no.nav.foreldrepenger.soknad.kontrakt.builder.UttakPeriodeBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Aktivitet;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Aktivitet.AktivitetType;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Arbeidsgiver;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Arbeidsgiver.ArbeidsgiverType;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Arbeidstidprosent;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Gradering;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.OverføringÅrsak;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Rolle;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UtsettelseÅrsak;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakDto;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakPeriodeDto;

public final class UttaksperioderMaler {

    private UttaksperioderMaler() {
    }

    public static UttakPeriodeDto uttaksperiode(Rolle rolle, KontoType konto, LocalDate fom, LocalDate tom) {
        return uttaksperiode(rolle, konto, fom, tom, null, null);
    }

    public static UttakPeriodeDto uttaksperiode(Rolle rolle, KontoType konto, LocalDate fom, LocalDate tom,
                                                    BigDecimal samtidigUttakProsent) {
        return uttaksperiode(rolle, konto, fom, tom, samtidigUttakProsent, null);
    }

    public static UttakPeriodeDto uttaksperiode(Rolle rolle, KontoType konto, LocalDate fom, LocalDate tom,
                                                    BigDecimal samtidigUttakProsent, MorsAktivitet morsAktivitet) {
        var uttak = UttakPeriodeBuilder.uttak(rolle)
                .medKontoType(konto)
                .medSamtidigUttak(samtidigUttakProsent)
                .medMorsAktivitet(morsAktivitet)
                .build();
        return periodeMedSøker(fom, tom, uttak);
    }

    public static UttakPeriodeDto uttaksperiode(Rolle rolle, KontoType konto, LocalDate fom, LocalDate tom,
                                                    UttaksperiodeType... uttaksperiodeTyper) {
        return uttaksperiode(rolle, konto, fom, tom, 100, uttaksperiodeTyper);
    }

    public static UttakPeriodeDto uttaksperiode(Rolle rolle, KontoType konto, LocalDate fom, LocalDate tom,
                                                    int uttaksprosent, UttaksperiodeType... uttaksperiodeTyper) {
        var periodetyper = Set.of(uttaksperiodeTyper);
        return periodeMedSøker(fom, tom, UttakPeriodeBuilder.uttak(rolle)
                .medKontoType(konto)
                .medFlerbarnsdager(periodetyper.contains(UttaksperiodeType.FLERBARNSDAGER))
                .medSamtidigUttak(periodetyper.contains(UttaksperiodeType.SAMTIDIGUTTAK) ? BigDecimal.valueOf(uttaksprosent) : null)
                .build());
    }

    public static UttakPeriodeDto graderingsperiodeArbeidstaker(Rolle rolle, KontoType konto, LocalDate fom, LocalDate tom,
                                                                    String arbeidsgiverIdentifikator, BigDecimal arbeidstidsprosent) {
        return graderingsperiodeArbeidstaker(rolle, konto, fom, tom, arbeidsgiverIdentifikator, arbeidstidsprosent, null);
    }

    public static UttakPeriodeDto graderingsperiodeArbeidstaker(Rolle rolle, KontoType konto, LocalDate fom, LocalDate tom,
                                                                    String arbeidsgiverIdentifikator, BigDecimal arbeidstidsprosent,
                                                                    MorsAktivitet morsAktivitet) {
        var gradering = new Gradering(new Arbeidstidprosent(arbeidstidsprosent),
                new Aktivitet(AktivitetType.ORDINÆRT_ARBEID,
                        new Arbeidsgiver(arbeidsgiverIdentifikator, ArbeidsgiverType.ORGANISASJON),
                        TestOrganisasjoner.navnFor(arbeidsgiverIdentifikator)));
        return periodeMedSøker(fom, tom, UttakPeriodeBuilder.uttak(rolle)
                .medKontoType(konto)
                .medGradering(gradering)
                .medMorsAktivitet(morsAktivitet)
                .build());
    }

    public static UttakPeriodeDto graderingsperiodeFL(Rolle rolle, KontoType konto, LocalDate fom, LocalDate tom,
                                                          BigDecimal arbeidstidsprosent) {
        return graderingsperiode(rolle, konto, fom, tom, arbeidstidsprosent, AktivitetType.FRILANS);
    }

    public static UttakPeriodeDto graderingsperiodeSN(Rolle rolle, KontoType konto, LocalDate fom, LocalDate tom,
                                                          BigDecimal arbeidstidsprosent) {
        return graderingsperiode(rolle, konto, fom, tom, arbeidstidsprosent, AktivitetType.SELVSTENDIG_NÆRINGSDRIVENDE);
    }

    private static UttakPeriodeDto graderingsperiode(Rolle rolle, KontoType konto, LocalDate fom, LocalDate tom,
                                                         BigDecimal arbeidstidsprosent, AktivitetType aktivitetType) {
        var gradering = new Gradering(new Arbeidstidprosent(arbeidstidsprosent), new Aktivitet(aktivitetType, null, null));
        return periodeMedSøker(fom, tom, UttakPeriodeBuilder.uttak(rolle)
                .medKontoType(konto)
                .medGradering(gradering)
                .build());
    }

    public static UttakPeriodeDto utsettelsesperiode(Rolle rolle, UtsettelseÅrsak årsak, LocalDate fom, LocalDate tom) {
        return utsettelsesperiode(rolle, årsak, fom, tom, null);
    }

    public static UttakPeriodeDto utsettelsesperiode(Rolle rolle, UtsettelseÅrsak årsak, LocalDate fom, LocalDate tom,
                                                        MorsAktivitet morsAktivitet) {
        return periodeMedSøker(fom, tom, UttakPeriodeBuilder.uttak(rolle)
                .medUtsettelseÅrsak(årsak)
                .medMorsAktivitet(morsAktivitet)
                .build());
    }

    public static UttakPeriodeDto overføringsperiode(Rolle rolle, OverføringÅrsak årsak, KontoType konto,
                                                         LocalDate fom, LocalDate tom) {
        return periodeMedSøker(fom, tom, UttakPeriodeBuilder.uttak(rolle)
                .medKontoType(konto)
                .medOverføringÅrsak(årsak)
                .build());
    }

    private static UttakPeriodeDto periodeMedSøker(LocalDate fom, LocalDate tom, UttakDto uttak) {
        var periode = justerPeriodeHelg(fom, tom);
        return UttakPeriodeBuilder.periode(periode.fom(), periode.tom())
                .medSøker(uttak)
                .build();
    }

    static Periode justerPeriodeHelg(LocalDate fom, LocalDate tom) {
        if (fom.plusDays(1).equals(tom)) {
            return new Periode(helgejustertTilFredag(fom), helgejustertTilMandag(tom));
        } else {
            return new Periode(helgejustertTilMandag(fom), helgejustertTilFredag(tom));
        }
    }

    public record Periode(LocalDate fom, LocalDate tom) {
    }
}
