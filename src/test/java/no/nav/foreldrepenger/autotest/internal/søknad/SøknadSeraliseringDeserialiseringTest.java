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
import static no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.TilretteleggingBuilder.delvis;
import static no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.TilretteleggingBuilder.hel;
import static no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.TilretteleggingBuilder.ingen;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.SøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.SøkerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.MedlemsskapMaler;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.OpptjeningMaler;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.TilretteleggingBehovBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.maler.ArbeidsforholdMaler;

public class SøknadSeraliseringDeserialiseringTest extends SerializationTestBase {

    private static SøknadDto foreldrepengesøknad;

    @BeforeAll
    public static void byggSøknadd(){
        foreldrepengesøknad = lagSøknadForeldrepengerFødsel(LocalDate.now().minusWeeks(4), MOR).build();

    }

    @Test
    public void søkerTest() {
        test(foreldrepengesøknad.søker());
    }

    @Test
    public void foreldrepengerYtelseTest() {
        test(foreldrepengesøknad);
    }

    @Test
    public void foreldrepengersøknadTest() {
        test(foreldrepengesøknad);
        test(lagSøknadForeldrepengerTermin(LocalDate.now().minusWeeks(4), MOR)
                .medMedlemsskap(MedlemsskapMaler.medlemskapUtlandetForrige12mnd())
                .build());
        test(lagSøknadForeldrepengerAdopsjon(LocalDate.now(), MOR, true)
                .medSøker(new SøkerBuilder(MOR).medSelvstendigNæringsdrivendeInformasjon(List.of(OpptjeningMaler.egenNaeringOpptjening("992261005"))).build())
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
    public void tilretteleggingTest() {
        test(hel(LocalDate.now(), LocalDate.now().plusDays(5), ArbeidsforholdMaler.virksomhet(new Orgnummer("992261005"))).build());
        test(delvis(LocalDate.now(), LocalDate.now().plusDays(5), ArbeidsforholdMaler.privatArbeidsgiver(new Fødselsnummer("12345678910")), 50.0).build());
        test(ingen(LocalDate.now(), LocalDate.now().plusDays(5), ArbeidsforholdMaler.selvstendigNæringsdrivende()).build());
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
