package no.nav.foreldrepenger.autotest.internal.søknad;

import static no.nav.foreldrepenger.common.domain.BrukerRolle.MOR;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadAdopsjon;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadOmsorg;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadEngangsstønadMaler.lagEngangstønadTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerAdopsjon;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadForeldrepengerMaler.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.maler.SøknadSvangerskapspengerMaler.lagSvangerskapspengerSøknad;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.SøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.TilretteleggingBehovBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.ArbeidsforholdMaler;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.OpptjeningMaler;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.UtenlandsoppholdMaler;

public class SøknadSeraliseringDeserialiseringTest extends SerializationTestBase {

    private static SøknadDto foreldrepengesøknad;

    @BeforeAll
    public static void byggSøknadd(){
        foreldrepengesøknad = lagSøknadForeldrepengerFødsel(LocalDate.now().minusWeeks(4), MOR).build();

    }

    @Test
    public void foreldrepengerYtelseTest() {
        test(foreldrepengesøknad);
    }

    @Test
    public void foreldrepengersøknadTest() {
        test(foreldrepengesøknad);
        test(lagSøknadForeldrepengerTermin(LocalDate.now().minusWeeks(4), MOR)
                .medUtenlandsopphold(UtenlandsoppholdMaler.oppholdIUtlandetForrige12mnd())
                .build());
        test(lagSøknadForeldrepengerAdopsjon(LocalDate.now(), MOR, true)
                .medSelvstendigNæringsdrivendeInformasjon(OpptjeningMaler.egenNaeringOpptjening("992261005"))
                .build());
    }

    @Test
    public void engangsstønadTest() {
        test(lagEngangstønadFødsel(LocalDate.now().minusWeeks(4)).build());
        test(lagEngangstønadTermin(LocalDate.now().plusWeeks(4)).build());
        test(lagEngangstønadAdopsjon(LocalDate.now(), false).build());
        test(lagEngangstønadOmsorg(LocalDate.now()).build());
    }

    @Test
    public void svangerskapspengersøknadTest() {
        var delvisTilrettelegging = new TilretteleggingBehovBuilder(ArbeidsforholdMaler.privatArbeidsgiver(new Fødselsnummer("12345678910")), LocalDate.now())
                .delvis(LocalDate.now().plusDays(5), 50.0)
                .build();
        test(lagSvangerskapspengerSøknad(LocalDate.now().plusWeeks(4), List.of(delvisTilrettelegging)).build());
    }

    @Test
    public void endringssøknadTest() {
    }
}
