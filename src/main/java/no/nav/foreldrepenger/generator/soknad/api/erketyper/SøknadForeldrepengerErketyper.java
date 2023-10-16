package no.nav.foreldrepenger.generator.soknad.api.erketyper;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.generator.soknad.api.builder.AnnenforelderBuilder;
import no.nav.foreldrepenger.generator.soknad.api.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.generator.soknad.api.builder.SøkerBuilder;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.Dekningsgrad;

public final class SøknadForeldrepengerErketyper {

    private SøknadForeldrepengerErketyper() {
    }

    private static ForeldrepengerBuilder lagSøknadForeldrepenger(LocalDate familiehendelse, BrukerRolle brukerRolle) {
        return new ForeldrepengerBuilder()
                .medFordeling(UttakErketyper.fordelingHappyCase(familiehendelse, brukerRolle))
                .medDekningsgrad(Dekningsgrad.HUNDRE)
                .medMedlemsskap(MedlemsskapErketyper.medlemsskapNorge())
                .medSøker(new SøkerBuilder(brukerRolle).build())
                .medAnnenForelder(AnnenforelderBuilder.ukjentForelder());
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTermin(LocalDate termindato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(termindato, brukerRolle)
                .medBarn(RelasjonTilBarnErketyper.termin(1, termindato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerFødsel(LocalDate fødselsdato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(fødselsdato, brukerRolle)
                .medBarn(RelasjonTilBarnErketyper.fødsel(1, fødselsdato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTerminFødsel(LocalDate fødselsdato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(fødselsdato, brukerRolle)
            .medBarn(RelasjonTilBarnErketyper.fødselMedTermin(1, fødselsdato, fødselsdato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTerminFødsel(LocalDate termindato, LocalDate fødselsdato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(termindato, brukerRolle)
            .medBarn(RelasjonTilBarnErketyper.fødselMedTermin(1, fødselsdato, termindato));
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerAdopsjon(LocalDate omsorgsovertakelsedatoen,
                                                                        BrukerRolle brukerRolle,
                                                                        Boolean ektefellesBarn) {
        return lagSøknadForeldrepenger(omsorgsovertakelsedatoen, brukerRolle)
                .medBarn(RelasjonTilBarnErketyper.adopsjon(omsorgsovertakelsedatoen, ektefellesBarn));
    }

}
