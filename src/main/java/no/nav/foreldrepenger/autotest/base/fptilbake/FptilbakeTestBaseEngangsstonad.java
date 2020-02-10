package no.nav.foreldrepenger.autotest.base.fptilbake;

import no.nav.foreldrepenger.autotest.aktoerer.fptilbake.TilbakekrevingSaksbehandler;
import no.nav.foreldrepenger.autotest.base.EngangsstonadTestBase;
import org.junit.jupiter.api.BeforeEach;

public class FptilbakeTestBaseEngangsstonad extends EngangsstonadTestBase {

    protected TilbakekrevingSaksbehandler tbksaksbehandler;

    @BeforeEach
    public void setUpTbkSaksbehandlerEN(){
        tbksaksbehandler = new TilbakekrevingSaksbehandler();
    }
}
