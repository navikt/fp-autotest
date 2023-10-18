package no.nav.foreldrepenger.generator.soknad.api.erketyper;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.generator.soknad.api.builder.EngangsstønadBuilder;
import no.nav.foreldrepenger.generator.soknad.api.builder.SøkerBuilder;

public final class SøknadEngangsstønadErketyper {

    private SøknadEngangsstønadErketyper() {
    }

    private static EngangsstønadBuilder lagEngangsstønad(BrukerRolle brukerRolle) {
        return new EngangsstønadBuilder()
                .medSøker(new SøkerBuilder(brukerRolle).build())
                .medMedlemsskap(MedlemsskapErketyper.medlemsskapNorge());
    }

    public static EngangsstønadBuilder lagEngangstønadFødsel(BrukerRolle brukerRolle, LocalDate familiehendelse) {
        return lagEngangsstønad(brukerRolle)
                .medBarn(RelasjonTilBarnErketyper.fødsel(1, familiehendelse));
    }

    public static EngangsstønadBuilder lagEngangstønadTermin(BrukerRolle brukerRolle, LocalDate familiehendelse) {
        return lagEngangsstønad(brukerRolle)
                .medBarn(RelasjonTilBarnErketyper.termin(1, familiehendelse));
    }

    public static EngangsstønadBuilder lagEngangstønadAdopsjon(BrukerRolle brukerRolle, LocalDate omsorgsovertakelsedato,
                                                               Boolean ektefellesBarn) {
        return lagEngangsstønad(brukerRolle)
                .medBarn(RelasjonTilBarnErketyper.adopsjon(omsorgsovertakelsedato, ektefellesBarn));
    }

    public static EngangsstønadBuilder lagEngangstønadOmsorg(BrukerRolle brukerRolle, LocalDate omsorgsovertakelsedato) {
        return lagEngangsstønad(brukerRolle)
                .medBarn(RelasjonTilBarnErketyper.omsorgsovertakelse(omsorgsovertakelsedato));
    }
}
