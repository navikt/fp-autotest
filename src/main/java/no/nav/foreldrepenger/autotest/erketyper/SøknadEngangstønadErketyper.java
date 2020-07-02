package no.nav.foreldrepenger.autotest.erketyper;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.OmsorgsovertakelseÅrsak;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;

public class SøknadEngangstønadErketyper {
    private static EngangstønadBuilder lagEngangstønad(String aktørID, SøkersRolle søkersRolle) {
        return new EngangstønadBuilder(aktørID, søkersRolle)
                .medMedlemskap(MedlemskapErketyper.medlemskapNorge());
    }

    public static EngangstønadBuilder lagEngangstønadFødsel(String aktørID, SøkersRolle søkersRolle,
            LocalDate familiehendelse) {
        return lagEngangstønad(aktørID, søkersRolle)
                .medSoekersRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(1, familiehendelse));
    }

    public static EngangstønadBuilder lagEngangstønadTermin(String aktørID, SøkersRolle søkersRolle,
            LocalDate familiehendelse) {
        return lagEngangstønad(aktørID, søkersRolle)
                .medSoekersRelasjonTilBarnet(RelasjonTilBarnetErketyper.termin(1, familiehendelse));
    }

    public static EngangstønadBuilder lagEngangstønadAdopsjon(String aktørID, SøkersRolle søkersRolle,
            Boolean ektefellesBarn) {
        return lagEngangstønad(aktørID, søkersRolle)
                .medSoekersRelasjonTilBarnet(RelasjonTilBarnetErketyper.adopsjon(ektefellesBarn));
    }

    public static EngangstønadBuilder lagEngangstønadOmsorg(String aktørID, SøkersRolle søkersRolle,
            OmsorgsovertakelseÅrsak årsak) {
        return lagEngangstønad(aktørID, søkersRolle)
                .medSoekersRelasjonTilBarnet(RelasjonTilBarnetErketyper.omsorgsovertakelse(årsak));

    }
}
