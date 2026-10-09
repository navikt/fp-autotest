package no.nav.foreldrepenger.autotest.internal.søknad;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.generator.soknad.maler.SøknadEndringMaler;
import no.nav.foreldrepenger.generator.soknad.maler.UttakMaler;
import no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.ForeldrepengesøknadDto;
import no.nav.foreldrepenger.soknad.kontrakt.builder.AnnenforelderBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.builder.BarnBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.EøsUttakDto;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Rolle;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakDto;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakPeriodeDto;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.UttaksplanDto;

/**
 * Fletting av begge foreldrenes perioder til én felles uttaksplan.
 */
@Tag("internal")
class FellesUttaksplanTest {

    @Test
    @DisplayName("Fordeling med liste og varargs gir samme uttaksplan med justeringsflagg false")
    @Description("Begge overloadene bevarer periodene og gir forventet DTO med justeringsflagg false.")
    void fordelingMedListeOgVarargsGirSammeUttaksplan() {
        var førstePeriode = mor("2024-01-01", "2024-01-12", KontoType.MØDREKVOTE);
        var andrePeriode = annenPart("2024-02-05", "2024-02-09", uttak(Rolle.FAR_MEDMOR, KontoType.FEDREKVOTE));
        var perioder = List.of(førstePeriode, andrePeriode);
        var forventet = new UttaksplanDto(false, List.of(), perioder);

        var fraListe = UttakMaler.fordeling(perioder);
        var fraVarargs = UttakMaler.fordeling(førstePeriode, andrePeriode);

        assertThat(fraListe).isEqualTo(forventet).isEqualTo(fraVarargs);
        assertThat(fraListe.ønskerJustertUttakVedFødsel()).isFalse();
        assertThat(fraVarargs.ønskerJustertUttakVedFødsel()).isFalse();
    }

    @Test
    @DisplayName("Perspektivbytte bevarer uttaksdata og bytter søker og annen part")
    void byttPerspektivBevarerUttaksdata() {
        var morsUttak = UttaksperioderMaler.graderingsperiodeFL(Rolle.MOR, KontoType.MØDREKVOTE, d("2024-01-01"), d("2024-01-12"),
                BigDecimal.valueOf(50)).søker();
        var farsUttak = UttaksperioderMaler.uttaksperiode(Rolle.FAR_MEDMOR, KontoType.FEDREKVOTE, d("2024-01-01"), d("2024-01-12"),
                BigDecimal.valueOf(50)).søker();
        var opprinneligePerioder = List.of(new UttakPeriodeDto(d("2024-01-01"), d("2024-01-12"), morsUttak, farsUttak, null),
                new UttakPeriodeDto(d("2024-01-13"), d("2024-01-14"), morsUttak, null, null),
                new UttakPeriodeDto(d("2024-02-05"), d("2024-02-09"), null, farsUttak, null));
        var plan = new ArrayList<>(opprinneligePerioder);

        var byttetPlan = UttakMaler.byttPerspektiv(plan);

        assertThat(byttetPlan).isNotSameAs(plan)
                .containsExactly(new UttakPeriodeDto(d("2024-01-01"), d("2024-01-12"), farsUttak, morsUttak, null),
                        new UttakPeriodeDto(d("2024-01-13"), d("2024-01-14"), null, morsUttak, null),
                        new UttakPeriodeDto(d("2024-02-05"), d("2024-02-09"), farsUttak, null, null));
        assertThat(plan).containsExactlyElementsOf(opprinneligePerioder);
        assertThat(UttakMaler.byttPerspektiv(byttetPlan)).containsExactlyElementsOf(opprinneligePerioder);
        plan.clear();
        assertThat(byttetPlan).hasSize(3);
    }

    @Test
    @DisplayName("Perspektivbytte fjerner EØS-uttak som gjelder opprinnelig annen part")
    void byttPerspektivFjernerAnnenPartsEøsUttak() {
        var morsUttak = uttak(Rolle.MOR, KontoType.MØDREKVOTE);
        var eøsUttak = new EøsUttakDto(KontoType.FELLESPERIODE, null);
        var periode = new UttakPeriodeDto(d("2024-01-01"), d("2024-01-12"), morsUttak, null, eøsUttak);

        var eøsPeriode = new UttakPeriodeDto(d("2024-02-05"), d("2024-02-09"), null, null, eøsUttak);

        var byttetPlan = UttakMaler.byttPerspektiv(List.of(periode, eøsPeriode));

        assertThat(byttetPlan).containsExactly(new UttakPeriodeDto(d("2024-01-01"), d("2024-01-12"), null, morsUttak, null));
        assertThat(periode.annenPartEøs()).isEqualTo(eøsUttak);
        assertThat(eøsPeriode.annenPartEøs()).isEqualTo(eøsUttak);
    }

    @Test
    @DisplayName("Perspektivbytte av en tom plan gir en tom plan")
    void byttPerspektivAvTomPlan() {
        assertThat(UttakMaler.byttPerspektiv(List.of())).isEmpty();
    }

    @Test
    @DisplayName("Første endringssøknad bruker den oppgitte planen uten å endre periodene")
    @Description("Ingen perioder fra førstegangssøknaden legges til. Rolle, barn og annen forelder videreføres.")
    void førsteEndringBrukerEksplisittFullPlan() {
        var farsUttak = uttak(Rolle.FAR_MEDMOR, KontoType.FEDREKVOTE);
        var søknad = (ForeldrepengesøknadDto) new ForeldrepengerBuilder()
                .medRolle(BrukerRolle.MOR)
                .medBarn(BarnBuilder.fødsel(1, d("2024-01-01")).build())
                .medAnnenForelder(AnnenforelderBuilder.ukjentForelder())
                .medPerioder(List.of(
                        mor("2024-01-01", "2024-01-26", KontoType.MØDREKVOTE),
                        annenPart("2024-03-04", "2024-03-29", farsUttak)))
                .build();
        var eksplisittPlan = new UttaksplanDto(false, List.of(), List.of(
                new UttakPeriodeDto(d("2024-01-15"), d("2024-01-19"), uttak(Rolle.MOR, KontoType.FELLESPERIODE), farsUttak, null),
                annenPart("2024-02-05", "2024-02-09", farsUttak)));

        var endring = SøknadEndringMaler.lagEndringssøknad(søknad, new Saksnummer("123456789"), eksplisittPlan).build();

        assertThat(endring.uttaksplan()).isEqualTo(eksplisittPlan);
        assertThat(endring.rolle()).isEqualTo(søknad.rolle());
        assertThat(endring.barn()).isEqualTo(søknad.barn());
        assertThat(endring.annenForelder()).isEqualTo(søknad.annenForelder());
    }

    @Test
    @DisplayName("Andre endringssøknad bruker bare de eksplisitt oppgitte periodene")
    @Description("Planen erstattes ved hver endring, mens rolle, barn og annen forelder videreføres fra forrige søknad.")
    void andreEndringBrukerEksplisittFullPlan() {
        var farsUttak = uttak(Rolle.FAR_MEDMOR, KontoType.FEDREKVOTE);
        var søknad = (ForeldrepengesøknadDto) new ForeldrepengerBuilder()
                .medRolle(BrukerRolle.FAR)
                .medBarn(BarnBuilder.fødsel(1, d("2024-01-01")).build())
                .medAnnenForelder(AnnenforelderBuilder.ukjentForelder())
                .medPerioder(List.of(new UttakPeriodeDto(d("2024-01-01"), d("2024-01-12"), farsUttak, null, null)))
                .build();
        var saksnummer = new Saksnummer("123456789");
        var førstePlan = List.of(
                new UttakPeriodeDto(d("2024-01-15"), d("2024-01-26"), farsUttak, uttak(Rolle.MOR, KontoType.MØDREKVOTE), null),
                annenPart("2024-03-04", "2024-03-29", uttak(Rolle.MOR, KontoType.FELLESPERIODE)));
        var andrePlan = List.of(
                new UttakPeriodeDto(d("2024-01-22"), d("2024-01-26"), uttak(Rolle.FAR_MEDMOR, KontoType.FELLESPERIODE), null, null),
                annenPart("2024-02-05", "2024-02-09", uttak(Rolle.MOR, KontoType.MØDREKVOTE)));

        var førsteEndring = SøknadEndringMaler.lagEndringssøknad(søknad, saksnummer, førstePlan).build();
        var andreEndring = SøknadEndringMaler.lagEndringssøknad(førsteEndring, saksnummer, andrePlan).build();

        assertThat(førsteEndring.uttaksplan().perioder()).isEqualTo(førstePlan);
        assertThat(andreEndring.uttaksplan().perioder()).isEqualTo(andrePlan);
        assertThat(førsteEndring.rolle()).isEqualTo(søknad.rolle());
        assertThat(førsteEndring.barn()).isEqualTo(søknad.barn());
        assertThat(førsteEndring.annenForelder()).isEqualTo(søknad.annenForelder());
        assertThat(andreEndring.rolle()).isEqualTo(førsteEndring.rolle());
        assertThat(andreEndring.barn()).isEqualTo(førsteEndring.barn());
        assertThat(andreEndring.annenForelder()).isEqualTo(førsteEndring.annenForelder());
    }

    @Test
    @DisplayName("Endringssøknaden bruker eksplisitt EØS-plan og ønske om justering ved fødsel")
    @Description("Oppgitt EØS-uttak brukes uendret uten å legge til EØS-perioder fra forrige søknad.")
    void endringBrukerEksplisittEøsPlanOgJusteringVedFødsel() {
        var eøsUttak = new EøsUttakDto(KontoType.FELLESPERIODE, null);
        var søknad = new ForeldrepengerBuilder().medPerioder(List.of(
                new UttakPeriodeDto(d("2024-01-01"), d("2024-01-26"), uttak(Rolle.MOR, KontoType.MØDREKVOTE), null, eøsUttak))).build();
        var eksplisittPlan = new UttaksplanDto(true, List.of(), List.of(
                new UttakPeriodeDto(d("2024-01-15"), d("2024-01-19"), uttak(Rolle.MOR, KontoType.FELLESPERIODE), null, eøsUttak),
                new UttakPeriodeDto(d("2024-02-05"), d("2024-02-09"), null, null, eøsUttak)));

        var endring = SøknadEndringMaler.lagEndringssøknad(søknad, new Saksnummer("123456789"), eksplisittPlan).build();

        assertThat(endring.uttaksplan()).isEqualTo(eksplisittPlan);
        assertThat(endring.uttaksplan().ønskerJustertUttakVedFødsel()).isTrue();
    }

    private static UttakPeriodeDto mor(String fom, String tom, KontoType konto) {
        return new UttakPeriodeDto(d(fom), d(tom), uttak(Rolle.MOR, konto), null, null);
    }

    private static UttakPeriodeDto annenPart(String fom, String tom, UttakDto uttak) {
        return new UttakPeriodeDto(d(fom), d(tom), null, uttak, null);
    }

    private static UttakDto uttak(Rolle rolle, KontoType konto) {
        return new UttakDto(rolle, konto, null, null, null, null, null, false, null);
    }

    private static LocalDate d(String dato) {
        return LocalDate.parse(dato);
    }
}
