package no.nav.foreldrepenger.generator.soknad.erketyper;

import java.time.LocalDate;

import no.nav.foreldrepenger.generator.soknad.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.generator.soknad.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.generator.soknad.erketyper.MedlemsskapErketyper;
import no.nav.foreldrepenger.generator.soknad.erketyper.RelasjonTilBarnErketyper;
import no.nav.foreldrepenger.generator.soknad.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.felles.annenforelder.UkjentForelder;
import no.nav.foreldrepenger.common.domain.felles.opptjening.Opptjening;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Dekningsgrad;

public final class SøknadForeldrepengerErketyper {

    private SøknadForeldrepengerErketyper() {
    }

    private static ForeldrepengerBuilder lagSøknadForeldrepenger(LocalDate familiehendelse, BrukerRolle brukerRolle) {
        return new ForeldrepengerBuilder(brukerRolle)
                .medFordeling(FordelingErketyper.fordelingHappyCase(familiehendelse, brukerRolle).build())
                .medDekningsgrad(Dekningsgrad.HUNDRE)
                .medMedlemsskap(MedlemsskapErketyper.medlemsskapNorge())
                .medOpptjening(new Opptjening(null, null, null, null))
                .medRettigheter(RettigheterErketyper.beggeForeldreRettIkkeAleneomsorg())
                .medAnnenForelder(new UkjentForelder());
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTermin(LocalDate termindato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(termindato, brukerRolle)
                .medRelasjonTilBarn(no.nav.foreldrepenger.generator.soknad.erketyper.RelasjonTilBarnErketyper.termin(1, termindato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerFødsel(LocalDate fødselsdato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(fødselsdato, brukerRolle)
                .medRelasjonTilBarn(no.nav.foreldrepenger.generator.soknad.erketyper.RelasjonTilBarnErketyper.fødsel(1, fødselsdato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTerminFødsel(LocalDate fødselsdato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(fødselsdato, brukerRolle)
            .medRelasjonTilBarn(no.nav.foreldrepenger.generator.soknad.erketyper.RelasjonTilBarnErketyper.fødselMedTermin(1, fødselsdato, fødselsdato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTerminFødsel(LocalDate termindato, LocalDate fødselsdato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(termindato, brukerRolle)
            .medRelasjonTilBarn(no.nav.foreldrepenger.generator.soknad.erketyper.RelasjonTilBarnErketyper.fødselMedTermin(1, fødselsdato, termindato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerAdopsjon(LocalDate omsorgsovertakelsedatoen,
                                                                        BrukerRolle brukerRolle,
                                                                        Boolean ektefellesBarn) {
        return lagSøknadForeldrepenger(omsorgsovertakelsedatoen, brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.adopsjon(omsorgsovertakelsedatoen, ektefellesBarn));
    }

}
