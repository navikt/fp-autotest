package no.nav.foreldrepenger.generator.soknad.maler;

import java.time.LocalDate;

import no.nav.foreldrepenger.kontrakter.fpsoknad.BrukerRolle;
import no.nav.foreldrepenger.kontrakter.fpsoknad.builder.BarnBuilder;
import no.nav.foreldrepenger.kontrakter.fpsoknad.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.Dekningsgrad;

public final class SøknadForeldrepengerMaler {

    private SøknadForeldrepengerMaler() {
    }

    private static ForeldrepengerBuilder lagSøknadForeldrepenger(LocalDate familiehendelse, BrukerRolle brukerRolle) {
        return new ForeldrepengerBuilder()
                .medRolle(brukerRolle)
                .medUttaksplan(UttakMaler.fordelingHappyCase(familiehendelse, brukerRolle))
                .medDekningsgrad(Dekningsgrad.HUNDRE)
                .medUtenlandsopphold(UtenlandsoppholdMaler.oppholdBareINorge())
                .medAnnenForelder(AnnenforelderMaler.ukjentForelder());
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTermin(LocalDate termindato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(termindato, brukerRolle)
                .medBarn(BarnBuilder.termin(1, termindato).build());
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerFødsel(LocalDate fødselsdato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(fødselsdato, brukerRolle)
                .medBarn(BarnBuilder.fødsel(1, fødselsdato).build());
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTerminFødsel(LocalDate fødselsdato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(fødselsdato, brukerRolle)
            .medBarn(BarnBuilder.fødsel(1, fødselsdato).build());
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerTerminFødsel(LocalDate termindato, LocalDate fødselsdato, BrukerRolle brukerRolle) {
        return lagSøknadForeldrepenger(termindato, brukerRolle)
            .medBarn(BarnBuilder.fødsel(1, fødselsdato)
                    .medTermindato(termindato)
                    .build());
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerAdopsjon(LocalDate omsorgsovertakelsedatoen,
                                                                        BrukerRolle brukerRolle,
                                                                        Boolean ektefellesBarn) {
        return lagSøknadForeldrepenger(omsorgsovertakelsedatoen, brukerRolle)
                .medBarn(BarnBuilder.adopsjon(omsorgsovertakelsedatoen, ektefellesBarn).build());
    }

}
