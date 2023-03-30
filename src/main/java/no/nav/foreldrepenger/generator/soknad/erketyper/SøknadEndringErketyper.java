package no.nav.foreldrepenger.generator.soknad.erketyper;


import java.time.LocalDate;

import no.nav.foreldrepenger.generator.soknad.builder.EndringssøknadBuilder;
import no.nav.foreldrepenger.generator.soknad.erketyper.RelasjonTilBarnErketyper;
import no.nav.foreldrepenger.generator.soknad.erketyper.RettigheterErketyper;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.Fordeling;

public class SøknadEndringErketyper {

    private SøknadEndringErketyper() {
        // Skal ikke instansieres
    }

    private static EndringssøknadBuilder lagEndringssøknad(BrukerRolle brukerrolle, Fordeling fordeling, Saksnummer saksnummer) {
        return new EndringssøknadBuilder(saksnummer, brukerrolle)
                .medFordeling(fordeling)
                .medRettigheter(RettigheterErketyper.beggeForeldreRettIkkeAleneomsorg());
    }

    public static EndringssøknadBuilder lagEndringssøknadFødsel(LocalDate fødselsdato, BrukerRolle brukerRolle, Fordeling fordeling, Saksnummer saksnummer) {
        return lagEndringssøknad(brukerRolle, fordeling, saksnummer)
                .medRelasjonTilBarn(RelasjonTilBarnErketyper.fødsel(1, fødselsdato));
    }
}
