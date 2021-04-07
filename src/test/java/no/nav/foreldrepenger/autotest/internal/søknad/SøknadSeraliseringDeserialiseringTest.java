package no.nav.foreldrepenger.autotest.internal.søknad;

import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadAdopsjon;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadFødsel;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadOmsorg;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadTermin;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerAdopsjon;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadSvangerskapspengerErketyper.lagSvangerskapspengerSøknad;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.TilretteleggingsErketyper.delvisTilrettelegging;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.TilretteleggingsErketyper.helTilrettelegging;
import static no.nav.foreldrepenger.autotest.søknad.erketyper.TilretteleggingsErketyper.ingenTilrettelegging;
import static no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle.MOR;
import static no.nav.foreldrepenger.autotest.søknad.modell.felles.relasjontilbarn.OmsorgsOvertakelsesÅrsak.SKAL_OVERTA_ALENE;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;
import no.nav.foreldrepenger.autotest.søknad.erketyper.ArbeidsforholdErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.MedlemsskapErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.OpptjeningErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.Søknad;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Orgnummer;

public class SøknadSeraliseringDeserialiseringTest extends SerializationTestBase {

    private static Søknad foreldrepengesøknad;

    @BeforeAll
    public static void byggSøknadd(){
        foreldrepengesøknad = lagSøknadForeldrepengerFødsel(LocalDate.now().minusWeeks(4), MOR).build();

    }

    @Test
    public void søkerTest() {
        test(foreldrepengesøknad.getSøker());
    }

    @Test
    public void foreldrepengerYtelseTest() {
        test(foreldrepengesøknad.getYtelse());
    }

    @Test
    public void foreldrepengersøknadTest() {
        test(foreldrepengesøknad);
        test(lagSøknadForeldrepengerTermin(LocalDate.now().minusWeeks(4), MOR)
                .medMedlemsskap(MedlemsskapErketyper.medlemskapUtlandetForrige12mnd())
                .build());
        test(lagSøknadForeldrepengerAdopsjon(LocalDate.now(), MOR, true)
                .medOpptjening(OpptjeningErketyper.medEgenNaeringOgFrilansOpptjening())
                .build());
    }

    @Test
    public void engangsstønadTest() {
        test(lagEngangstønadFødsel(MOR, LocalDate.now().minusWeeks(4)).build());
        test(lagEngangstønadTermin(MOR, LocalDate.now().plusWeeks(4)).build());
        test(lagEngangstønadAdopsjon(MOR, LocalDate.now(), false).build());
        test(lagEngangstønadOmsorg(MOR, LocalDate.now(), SKAL_OVERTA_ALENE).build());
    }

    @Test
    public void tilretteleggingTest() {
        test(helTilrettelegging(LocalDate.now(), LocalDate.now().plusDays(5), ArbeidsforholdErketyper.virksomhet(new Orgnummer("999035462"))));
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
