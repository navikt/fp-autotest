package no.nav.foreldrepenger.autotest.base.fptilbake;

import no.nav.foreldrepenger.autotest.aktoerer.fptilbake.TilbakekrevingSaksbehandler;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import org.junit.jupiter.api.BeforeEach;

public class FptilbakeTestBaseForeldrepenger extends ForeldrepengerTestBase {

    protected TilbakekrevingSaksbehandler tbksaksbehandler;
    protected TilbakekrevingSaksbehandler tbkbeslutter;

    @BeforeEach
    public void setUpTbkSaksbehandlerFP(){

        tbksaksbehandler = new TilbakekrevingSaksbehandler();
        tbkbeslutter = new TilbakekrevingSaksbehandler();
    }
}
