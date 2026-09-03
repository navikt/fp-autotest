package no.nav.foreldrepenger.generator.familie;

import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel;
import static no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner.NAV_OSLO;
import static no.nav.foreldrepenger.generator.familie.generator.TestOrganisasjoner.NAV_STORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.generator.familie.generator.InntektGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektYtelseBundle;
import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.SøkerDto;
import no.nav.foreldrepenger.soknad.kontrakt.opptjening.NæringDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.BrregDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PersonDto;

@Tag("internal")
class SøkerTest {

    private static final Fødselsnummer FØDSELSNUMMER = new Fødselsnummer("12345678910");
    private static final AktørId AKTØR_ID = new AktørId("9912345678910");
    private static final Saksnummer SAKSNUMMER = new Saksnummer("123456789");

    @Test
    @DisplayName("Registrerte virksomheter fra Brreg legges automatisk i søkerinfo")
    void registrerteVirksomheterLeggesISøkerinfo() {
        var virksomheter = List.of(
                virksomhet("999999991", "01"),
                virksomhet("999999992", "01.6"),
                virksomhet("999999993", "01.7"),
                virksomhet("999999994", "02.1"),
                virksomhet("999999995", "03.1"),
                virksomhet("999999996", "88.91"),
                virksomhet("999999997", "62.01"));
        var person = personMedBrreg(new BrregDto(virksomheter));

        var søkerinfo = søkerinfo(person);

        assertThat(søkerinfo.selvstendigNæring())
                .extracting(SøkerDto.SelvstendigNæring::næringstype)
                .containsExactly(
                        NæringDto.Virksomhetstype.JORDBRUK_SKOGBRUK,
                        NæringDto.Virksomhetstype.ANNEN,
                        NæringDto.Virksomhetstype.ANNEN,
                        NæringDto.Virksomhetstype.JORDBRUK_SKOGBRUK,
                        NæringDto.Virksomhetstype.FISKE,
                        NæringDto.Virksomhetstype.DAGMAMMA,
                        NæringDto.Virksomhetstype.ANNEN);
        assertThat(søkerinfo.selvstendigNæring().getFirst())
                .extracting(
                        SøkerDto.SelvstendigNæring::navn,
                        næring -> næring.organisasjonsnummer().value())
                .containsExactly("Virksomhet 999999991", "999999991");
    }

    @Test
    @DisplayName("Manglende inntektsytelse, Brreg eller virksomhetsliste gir tom selvstendig næring")
    void manglendeBrregdataGirTomListe() {
        assertThat(søkerinfo(PersonDto.builder().build()).selvstendigNæring()).isEmpty();
        assertThat(søkerinfo(personMedBrreg(null)).selvstendigNæring()).isEmpty();
        assertThat(søkerinfo(personMedBrreg(new BrregDto(null))).selvstendigNæring()).isEmpty();
    }

    @Test
    @DisplayName("Flere registrerte næringer beholdes i rekkefølge i søkerinfo")
    void flereRegistrerteNæringerLeggesISøkerinfo() {
        var person = personMedInntekt(InntektGenerator.ny()
                        .selvstendigNæringsdrivende(350_000)
                        .registrertNæring(
                                "974760673",
                                "VTP GÅRDSDRIFT",
                                "ENK",
                                "Enkeltpersonforetak",
                                "01.110",
                                "Dyrking av korn")
                        .build());

        assertThat(søkerinfo(person).selvstendigNæring())
                .extracting(
                        SøkerDto.SelvstendigNæring::navn,
                        næring -> næring.organisasjonsnummer().value(),
                        SøkerDto.SelvstendigNæring::næringstype)
                .containsExactly(
                        tuple("VTP FISKE", "999999999", NæringDto.Virksomhetstype.FISKE),
                        tuple("VTP GÅRDSDRIFT", "974760673", NæringDto.Virksomhetstype.JORDBRUK_SKOGBRUK));
    }

    @Test
    @DisplayName("Ordinære arbeidsforhold og frilansoppdrag legges i hver sin søkerinfo-liste")
    void aaregdataFordelesPåArbeidsforholdOgFrilansoppdrag() {
        var person = personMedInntekt(InntektGenerator.ny()
                        .arbeidsforhold(NAV_OSLO, 75, LocalDate.now().minusYears(1), 480_000)
                        .frilans(NAV_STORD, "frilans-1", 25, LocalDate.now().minusMonths(6),
                                LocalDate.now().minusMonths(1), 120_000)
                        .build());

        var søkerinfo = søkerinfo(person);

        assertThat(søkerinfo.arbeidsforhold())
                .singleElement()
                .extracting(SøkerDto.Arbeidsforhold::navn)
                .isEqualTo("NAV FAMILIE- OG PENSJONSYTELSER OSLO");
        assertThat(søkerinfo.frilansoppdrag())
                .singleElement()
                .extracting(
                        SøkerDto.Frilansoppdrag::navn,
                        SøkerDto.Frilansoppdrag::fom,
                        SøkerDto.Frilansoppdrag::tom)
                .containsExactly(
                        "NAV FAMILIE- OG PENSJONSYTELSER STORD",
                        LocalDate.now().minusMonths(6),
                        LocalDate.now().minusMonths(1));
    }

    @Test
    @DisplayName("Mange frilansoppdrag beholdes med perioder og rekkefølge i søkerinfo")
    void mangeFrilansoppdragLeggesISøkerinfo() {
        var inntektYtelse = InntektGenerator.ny();
        var førsteOppdragFom = LocalDate.now().minusDays(320);
        for (int i = 0; i < 20; i++) {
            var fom = førsteOppdragFom.plusDays(i * 16L);
            inntektYtelse.frilans(NAV_STORD, "frilans-" + (i + 1), 25, fom, fom.plusDays(13), 120_000);
        }
        var person = personMedInntekt(inntektYtelse.build());

        var frilansoppdrag = søkerinfo(person).frilansoppdrag();

        assertThat(frilansoppdrag).hasSize(20)
                .allSatisfy(oppdrag -> assertThat(oppdrag.navn())
                        .isEqualTo("NAV FAMILIE- OG PENSJONSYTELSER STORD"));
        assertThat(frilansoppdrag.getFirst())
                .extracting(SøkerDto.Frilansoppdrag::fom, SøkerDto.Frilansoppdrag::tom)
                .containsExactly(førsteOppdragFom, førsteOppdragFom.plusDays(13));
        assertThat(frilansoppdrag.getLast())
                .extracting(SøkerDto.Frilansoppdrag::fom, SøkerDto.Frilansoppdrag::tom)
                .containsExactly(førsteOppdragFom.plusDays(19 * 16L), førsteOppdragFom.plusDays(19 * 16L + 13));
    }

    private static BrregDto.VirksomhetDto virksomhet(String orgnummer, String næringskode) {
        return new BrregDto.VirksomhetDto(
                orgnummer,
                "Virksomhet " + orgnummer,
                "ENK",
                "Enkeltpersonforetak",
                næringskode,
                "Næring");
    }

    private static PersonDto personMedBrreg(BrregDto brreg) {
        return PersonDto.builder()
                .brreg(brreg)
                .build();
    }

    private static PersonDto personMedInntekt(InntektYtelseBundle inntekt) {
        return PersonDto.builder()
                .arbeidsforhold(inntekt.arbeidsforhold())
                .inntekt(inntekt.inntekt())
                .skatteopplysninger(inntekt.skatteopplysninger())
                .brreg(inntekt.brreg())
                .build();
    }

    private static SøkerDto søkerinfo(PersonDto person) {
        var søker = søker(person);
        søker.søk(lagSøknadForeldrepengerTerminFødsel(LocalDate.now().plusWeeks(3), BrukerRolle.MOR));
        return søker.førstegangssøknad().søkerinfo();
    }

    private static Søker søker(PersonDto person) {
        var innsender = (Innsender) Proxy.newProxyInstance(
                Innsender.class.getClassLoader(),
                new Class<?>[]{Innsender.class},
                (proxy, method, args) -> method.getReturnType().equals(Saksnummer.class) ? SAKSNUMMER : null);
        return new Mor(FØDSELSNUMMER, AKTØR_ID, AKTØR_ID, person, Map.of(), innsender);
    }
}
