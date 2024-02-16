package no.nav.foreldrepenger.generator.soknad.maler;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.UtenlandsoppholdMaler;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.BarnBuilder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.v2.util.builder.EngangsstønadBuilder;

public final class SøknadEngangsstønadMaler {

    private SøknadEngangsstønadMaler() {
    }

    private static EngangsstønadBuilder lagEngangsstønad() {
        return new EngangsstønadBuilder()
                .medSpråkkode(Målform.standard())
                .medUtenlandsopphold(UtenlandsoppholdMaler.oppholdBareINorge());
    }

    public static EngangsstønadBuilder lagEngangstønadFødsel(LocalDate familiehendelse) {
        return lagEngangsstønad()
                .medBarn(BarnBuilder.fødsel(1, familiehendelse).build());
    }

    public static EngangsstønadBuilder lagEngangstønadTermin(LocalDate familiehendelse) {
        return lagEngangsstønad()
                .medBarn(BarnBuilder.termin(1, familiehendelse).build());
    }

    public static EngangsstønadBuilder lagEngangstønadAdopsjon(LocalDate omsorgsovertakelsedato, Boolean ektefellesBarn) {
        return lagEngangsstønad()
                .medBarn(BarnBuilder.adopsjon(omsorgsovertakelsedato, ektefellesBarn).build());
    }

    public static EngangsstønadBuilder lagEngangstønadOmsorg(LocalDate omsorgsovertakelsedato) {
        return lagEngangsstønad()
                .medBarn(BarnBuilder.omsorgsovertakelse(omsorgsovertakelsedato).build());
    }
}
