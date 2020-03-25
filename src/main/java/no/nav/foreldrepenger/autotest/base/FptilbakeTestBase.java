package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.aktoerer.fptilbake.TilbakekrevingSaksbehandler;
import org.junit.jupiter.api.BeforeEach;

public class FptilbakeTestBase extends FpsakTestBase {

    protected TilbakekrevingSaksbehandler tbksaksbehandler;
    protected TilbakekrevingSaksbehandler tbkbeslutter;

    @BeforeEach
    public void setUpTbkSaksbehandlerSVP(){

        tbksaksbehandler = new TilbakekrevingSaksbehandler();
        tbkbeslutter = new TilbakekrevingSaksbehandler();
    }
}
