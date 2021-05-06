package no.nav.foreldrepenger.autotest.søknad.builderV2;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.søknad.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.søknad.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.MedlemsskapErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.RelasjonTilBarnErketyper;
import no.nav.foreldrepenger.autotest.søknad.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.Dekningsgrad;

public final class SøknadForeldrepengerErketyper {

    private SøknadForeldrepengerErketyper() {
    }

    private static ForeldrepengerBuilder lagSøknadForeldrepenger(LocalDate familiehendelse, Fødselsnummer fnrAnnenpart, BrukerRolle brukerRolle) {
        return new ForeldrepengerBuilder(brukerRolle)
                .medFordeling(FordelingErketyper.fordelingHappyCase(familiehendelse, brukerRolle))
                .medDekningsgrad(Dekningsgrad.GRAD100)
                .medMedlemsskap(MedlemsskapErketyper.medlemsskapNorge())
                .medOpptjening(Opptjening.builder().build())
                .medRettigheter(RettigheterErketyper.beggeForeldreRettIkkeAleneomsorg())
                .medAnnenForelder(new NorskForelder(fnrAnnenpart, ""));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTermin(LocalDate termindato, Fødselsnummer fnrAnnenpart, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(termindato, fnrAnnenpart, brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.termin(1, termindato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerFødsel(LocalDate fødselsdato, Fødselsnummer fnrAnnenpart, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(fødselsdato, fnrAnnenpart, brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.fødsel(1, fødselsdato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerAdopsjon(LocalDate omsorgsovertakelsedatoen,
                                                                        Fødselsnummer fnrAnnenpart,
                                                                        BrukerRolle brukerRolle,
                                                                        Boolean ektefellesBarn) {
        return lagSøknadForeldrepenger(omsorgsovertakelsedatoen, fnrAnnenpart, brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.adopsjon(omsorgsovertakelsedatoen, ektefellesBarn));
    }

}
