package no.nav.foreldrepenger.autotest.søknad.erketyper;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.søknad.builder.EngangsstønadBuilder;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.relasjontilbarn.OmsorgsOvertakelsesÅrsak;

public final class SøknadEngangsstønadErketyper {

    private SøknadEngangsstønadErketyper() {
    }

    private static EngangsstønadBuilder lagEngangsstønad(BrukerRolle brukerRolle) {
        return new EngangsstønadBuilder(brukerRolle)
                .medMedlemsskap(MedlemsskapErketyper.medlemsskapNorge());
    }

    public static EngangsstønadBuilder lagEngangstønadFødsel(BrukerRolle brukerRolle,
                                                             LocalDate familiehendelse) {
        return lagEngangsstønad(brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.fødsel(1, familiehendelse));
    }

    public static EngangsstønadBuilder lagEngangstønadTermin(BrukerRolle brukerRolle, LocalDate familiehendelse) {
        return lagEngangsstønad(brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.termin(1, familiehendelse));
    }

    public static EngangsstønadBuilder lagEngangstønadAdopsjon(BrukerRolle brukerRolle,
                                                               LocalDate omsorgsovertakelsedato, Boolean ektefellesBarn) {
        return lagEngangsstønad(brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.adopsjon(omsorgsovertakelsedato, ektefellesBarn));
    }

    public static EngangsstønadBuilder lagEngangstønadOmsorg(BrukerRolle brukerRolle,
                                                             LocalDate omsorgsovertakelsedato,
                                                             OmsorgsOvertakelsesÅrsak årsak) {
        return lagEngangsstønad(brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.omsorgsovertakelse(omsorgsovertakelsedato, årsak));
    }
}
