package no.nav.foreldrepenger.generator.soknad.erketyper;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.generator.soknad.builder.EngangsstønadBuilder;

public final class SøknadEngangsstønadErketyper {

    private SøknadEngangsstønadErketyper() {
    }

    private static EngangsstønadBuilder lagEngangsstønad(BrukerRolle brukerRolle) {
        return new EngangsstønadBuilder(brukerRolle)
                .medMedlemsskap(MedlemsskapErketyper.medlemsskapNorge());
    }

    public static EngangsstønadBuilder lagEngangstønadFødsel(BrukerRolle brukerRolle, LocalDate familiehendelse) {
        return lagEngangsstønad(brukerRolle)
                .medRelasjonTilBarn(no.nav.foreldrepenger.generator.soknad.erketyper.RelasjonTilBarnErketyper.fødsel(1, familiehendelse));
    }

    public static EngangsstønadBuilder lagEngangstønadTermin(BrukerRolle brukerRolle, LocalDate familiehendelse) {
        return lagEngangsstønad(brukerRolle)
                .medRelasjonTilBarn(no.nav.foreldrepenger.generator.soknad.erketyper.RelasjonTilBarnErketyper.termin(1, familiehendelse));
    }

    public static EngangsstønadBuilder lagEngangstønadAdopsjon(BrukerRolle brukerRolle, LocalDate omsorgsovertakelsedato,
                                                               Boolean ektefellesBarn) {
        return lagEngangsstønad(brukerRolle)
                .medRelasjonTilBarn(no.nav.foreldrepenger.generator.soknad.erketyper.RelasjonTilBarnErketyper.adopsjon(omsorgsovertakelsedato, ektefellesBarn));
    }

    public static EngangsstønadBuilder lagEngangstønadOmsorg(BrukerRolle brukerRolle, LocalDate omsorgsovertakelsedato) {
        return lagEngangsstønad(brukerRolle)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.omsorgsovertakelse(omsorgsovertakelsedato));
    }
}
