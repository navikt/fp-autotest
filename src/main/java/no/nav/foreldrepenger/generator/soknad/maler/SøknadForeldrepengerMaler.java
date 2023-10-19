package no.nav.foreldrepenger.generator.soknad.maler;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.Dekningsgrad;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.AnnenforelderBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.BarnBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.SøkerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.MedlemsskapMaler;

public final class SøknadForeldrepengerMaler {

    private SøknadForeldrepengerMaler() {
    }

    private static ForeldrepengerBuilder lagSøknadForeldrepenger(LocalDate familiehendelse, BrukerRolle brukerRolle) {
        return new ForeldrepengerBuilder()
                .medFordeling(UttakMaler.fordelingHappyCase(familiehendelse, brukerRolle))
                .medDekningsgrad(Dekningsgrad.HUNDRE)
                .medMedlemsskap(MedlemsskapMaler.medlemsskapNorge())
                .medSøker(new SøkerBuilder(brukerRolle).build())
                .medAnnenForelder(AnnenforelderBuilder.ukjentForelder());
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
                    .medTerminbekreftelseDato(termindato.minusMonths(1))
                    .build());
    }

    public static ForeldrepengerBuilder lagSøknadForeldrepengerAdopsjon(LocalDate omsorgsovertakelsedatoen,
                                                                        BrukerRolle brukerRolle,
                                                                        Boolean ektefellesBarn) {
        return lagSøknadForeldrepenger(omsorgsovertakelsedatoen, brukerRolle)
                .medBarn(BarnBuilder.adopsjon(omsorgsovertakelsedatoen, ektefellesBarn).build());
    }

}
