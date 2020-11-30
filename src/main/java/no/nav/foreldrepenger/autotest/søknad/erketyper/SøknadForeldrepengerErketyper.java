package no.nav.foreldrepenger.autotest.søknad.erketyper;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.søknad.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.Dekningsgrad;

public class SøknadForeldrepengerErketyper {

    private static ForeldrepengerBuilder lagSøknadForeldrepenger(LocalDate familiehendelse, BrukerRolle brukerRolle) {
        return new ForeldrepengerBuilder(brukerRolle)
                .medFordeling(FordelingErketyper.fordelingHappyCase(familiehendelse, brukerRolle))
                .medDekningsgrad(Dekningsgrad.GRAD100)
                .medMedlemsskap(MedlemsskapErketyper.medlemsskapNorge())
                .medOpptjening(Opptjening.builder().build())
                .medRettigheter(RettigheterErketyper.beggeForeldreRettIkkeAleneomsorg())
                .medAnnenForelder(new UkjentForelder());
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTermin(LocalDate termindato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(termindato, brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.termin(1, termindato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerFødsel(LocalDate fødselsdato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(fødselsdato, brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.fødsel(1, fødselsdato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerAdopsjon(LocalDate omsorgsovertakelsedatoen,
                                                                        BrukerRolle brukerRolle,
                                                                        Boolean ektefellesBarn) {
        return lagSøknadForeldrepenger(omsorgsovertakelsedatoen, brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.adopsjon(omsorgsovertakelsedatoen, ektefellesBarn));
    }

}
