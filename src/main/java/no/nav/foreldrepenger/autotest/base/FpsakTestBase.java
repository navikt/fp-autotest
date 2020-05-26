package no.nav.foreldrepenger.autotest.base;

import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.fordel.Fordel;
import no.nav.foreldrepenger.autotest.aktoerer.foreldrepenger.Saksbehandler;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kodeverk;

public class FpsakTestBase extends TestScenarioTestBase {

    /*
     * Aktører
     */
    protected Fordel fordel;
    protected Saksbehandler saksbehandler;
    protected Saksbehandler overstyrer;
    protected Saksbehandler beslutter;
    protected Saksbehandler klagebehandler;

    @BeforeEach
    public void setUp() {
        fordel = new Fordel();
        saksbehandler = new Saksbehandler();
        overstyrer = new Saksbehandler();
        beslutter = new Saksbehandler();
        klagebehandler = new Saksbehandler();
    }

    protected Kodeverk hentKodeverk() {
        if (saksbehandler != null && saksbehandler.kodeverk != null) {
            return saksbehandler.kodeverk;
        }
        return null;
    }

}
