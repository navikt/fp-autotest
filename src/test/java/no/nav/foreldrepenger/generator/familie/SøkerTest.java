package no.nav.foreldrepenger.generator.familie;

import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTerminFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.SøkerDto;
import no.nav.foreldrepenger.soknad.kontrakt.opptjening.NæringDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.AaregDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.BrregDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.InntektYtelseModellDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.PersonDto;

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
                .inntektytelse(InntektYtelseModellDto.builder()
                        .aareg(new AaregDto(List.of()))
                        .brreg(brreg)
                        .build())
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
