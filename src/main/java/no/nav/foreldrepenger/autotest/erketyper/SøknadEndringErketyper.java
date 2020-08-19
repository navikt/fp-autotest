package no.nav.foreldrepenger.autotest.erketyper;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EndringssøknadBuilder;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;

public class SøknadEndringErketyper {
    public static EndringssøknadBuilder lagEndringssøknad(String aktoerId, SøkersRolle søkersRolle, Fordeling fordeling,
            long saksnummer) {
        return new EndringssøknadBuilder(aktoerId, søkersRolle)
                .medFordeling(fordeling)
                .medSaksnummer(String.valueOf(saksnummer));
    }
}
