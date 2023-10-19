package no.nav.foreldrepenger.generator.soknad.maler;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.BarnBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.EngangsstønadBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.SøkerBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.MedlemsskapMaler;

public final class SøknadEngangsstønadMaler {

    private SøknadEngangsstønadMaler() {
    }

    private static EngangsstønadBuilder lagEngangsstønad(BrukerRolle brukerRolle) {
        return new EngangsstønadBuilder()
                .medSøker(new SøkerBuilder(brukerRolle).build())
                .medMedlemsskap(MedlemsskapMaler.medlemsskapNorge());
    }

    public static EngangsstønadBuilder lagEngangstønadFødsel(BrukerRolle brukerRolle, LocalDate familiehendelse) {
        return lagEngangsstønad(brukerRolle)
                .medBarn(BarnBuilder.fødsel(1, familiehendelse).build());
    }

    public static EngangsstønadBuilder lagEngangstønadTermin(BrukerRolle brukerRolle, LocalDate familiehendelse) {
        return lagEngangsstønad(brukerRolle)
                .medBarn(BarnBuilder.termin(1, familiehendelse).build());
    }

    public static EngangsstønadBuilder lagEngangstønadAdopsjon(BrukerRolle brukerRolle, LocalDate omsorgsovertakelsedato,
                                                               Boolean ektefellesBarn) {
        return lagEngangsstønad(brukerRolle)
                .medBarn(BarnBuilder.adopsjon(omsorgsovertakelsedato, ektefellesBarn).build());
    }

    public static EngangsstønadBuilder lagEngangstønadOmsorg(BrukerRolle brukerRolle, LocalDate omsorgsovertakelsedato) {
        return lagEngangsstønad(brukerRolle)
                .medBarn(BarnBuilder.omsorgsovertakelse(omsorgsovertakelsedato).build());
    }
}
