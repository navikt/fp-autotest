package no.nav.foreldrepenger.generator.soknad.maler;

import java.time.LocalDate;

import no.nav.foreldrepenger.common.oppslag.dkif.Målform;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.BarnV2Builder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.EngangsstønadV2Builder;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.maler.UtenlandsoppholdMaler;

public final class SøknadEngangsstønadMaler {

    private SøknadEngangsstønadMaler() {
    }

    private static EngangsstønadV2Builder lagEngangsstønad() {
        return new EngangsstønadV2Builder()
                .medSpråkkode(Målform.standard())
                .medUtenlandsopphold(UtenlandsoppholdMaler.oppholdBareINorge());
    }

    public static EngangsstønadV2Builder lagEngangstønadFødsel(LocalDate familiehendelse) {
        return lagEngangsstønad()
                .medBarn(BarnV2Builder.fødsel(1, familiehendelse).build());
    }

    public static EngangsstønadV2Builder lagEngangstønadTermin(LocalDate familiehendelse) {
        return lagEngangsstønad()
                .medBarn(BarnV2Builder.termin(1, familiehendelse).build());
    }

    public static EngangsstønadV2Builder lagEngangstønadAdopsjon(LocalDate omsorgsovertakelsedato, Boolean ektefellesBarn) {
        return lagEngangsstønad()
                .medBarn(BarnV2Builder.adopsjon(omsorgsovertakelsedato, ektefellesBarn).build());
    }

    public static EngangsstønadV2Builder lagEngangstønadOmsorg(LocalDate omsorgsovertakelsedato) {
        return lagEngangsstønad()
                .medBarn(BarnV2Builder.omsorgsovertakelse(omsorgsovertakelsedato).build());
    }
}
