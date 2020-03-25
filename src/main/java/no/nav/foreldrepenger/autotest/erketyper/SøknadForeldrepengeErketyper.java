package no.nav.foreldrepenger.autotest.erketyper;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.UkjentForelder;

import java.time.LocalDate;

public class SøknadForeldrepengeErketyper {

    public static ForeldrepengerBuilder lagSøknadForeldrepenger(LocalDate familiehendelse, String søkerAktørId, SøkersRolle søkersRolle) {
        return new ForeldrepengerBuilder(søkerAktørId, søkersRolle)
                .medFordeling(FordelingErketyper.fordelingHappyCase(familiehendelse, søkersRolle))
                .medDekningsgrad("100")
                .medMedlemskap(MedlemskapErketyper.medlemskapNorge())
                .medRettigheter(RettigheterErketyper.beggeForeldreRettIkkeAleneomsorg())
                .medAnnenForelder(new UkjentForelder());
    }
    public static ForeldrepengerBuilder lagSøknadForeldrepengerTermin(LocalDate termindato, String søkerAktørId, SøkersRolle søkersRolle) {
        return lagSøknadForeldrepenger(termindato, søkerAktørId, søkersRolle)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.termin(1, termindato));

    }
    public static ForeldrepengerBuilder lagSøknadForeldrepengerFødsel(LocalDate fødselsdato, String søkerAktørId, SøkersRolle søkersRolle) {
        return lagSøknadForeldrepenger(fødselsdato, søkerAktørId, søkersRolle)
                .medRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(1, fødselsdato));

    }

}
