package no.nav.foreldrepenger.autotest.internal.søknad;

import static no.nav.foreldrepenger.common.domain.BrukerRolle.MOR;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadAdopsjon;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadFødsel;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadOmsorg;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadTermin;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerAdopsjon;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.TilretteleggingsErketyper.delvisTilrettelegging;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.TilretteleggingsErketyper.helTilrettelegging;
import static no.nav.foreldrepenger.generator.soknad.api.erketyper.TilretteleggingsErketyper.ingenTilrettelegging;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.generator.soknad.api.builder.SøkerBuilder;
import no.nav.foreldrepenger.generator.soknad.api.dto.SøknadDto;
import no.nav.foreldrepenger.generator.soknad.api.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.generator.soknad.api.erketyper.MedlemsskapErketyper;
import no.nav.foreldrepenger.generator.soknad.api.erketyper.OpptjeningErketyper;

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
                .medMedlemsskap(MedlemsskapErketyper.medlemskapUtlandetForrige12mnd())
                .build());
        test(lagSøknadForeldrepengerAdopsjon(LocalDate.now(), MOR, true)
                .medSøker(new SøkerBuilder(MOR).medSelvstendigNæringsdrivendeInformasjon(List.of(OpptjeningErketyper.egenNaeringOpptjening("992261005"))).build())
                .build());
    }

    @Test
    public void engangsstønadTest() {
        test(lagEngangstønadFødsel(MOR, LocalDate.now().minusWeeks(4)).build());
        test(lagEngangstønadTermin(MOR, LocalDate.now().plusWeeks(4)).build());
        test(lagEngangstønadAdopsjon(MOR, LocalDate.now(), false).build());
        test(lagEngangstønadOmsorg(MOR, LocalDate.now()).build());
    }

    @Test
    public void tilretteleggingTest() {
        test(helTilrettelegging(LocalDate.now(), LocalDate.now().plusDays(5), ArbeidsforholdErketyper.virksomhet(new Orgnummer("992261005"))));
        test(delvisTilrettelegging(LocalDate.now(), LocalDate.now().plusDays(5), ArbeidsforholdErketyper.privatArbeidsgiver("12345678910"), 50));
        test(ingenTilrettelegging(LocalDate.now(), LocalDate.now().plusDays(5), ArbeidsforholdErketyper.selvstendigNæringsdrivende()));
    }

    @Test
    public void svangerskapspengersøknadTest() {
        var delvisTilrettelegging = delvisTilrettelegging(LocalDate.now(), LocalDate.now().plusDays(5), ArbeidsforholdErketyper.privatArbeidsgiver("12345678910"), 50);
        test(lagSvangerskapspengerSøknad(MOR, LocalDate.now().plusWeeks(4), List.of(delvisTilrettelegging)).build());
    }

    @Test
    public void endringssøknadTest() {
    }
}
