package no.nav.foreldrepenger.autotest.base;

import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.fptilbake.TilbakekrevingSaksbehandler;

public class FptilbakeTestBase extends FpsakTestBase {

    protected TilbakekrevingSaksbehandler tbksaksbehandler;
    protected TilbakekrevingSaksbehandler tbkbeslutter;

    @BeforeEach
    public void setUpTbkSaksbehandler() {

        tbksaksbehandler = new TilbakekrevingSaksbehandler();
        tbkbeslutter = new TilbakekrevingSaksbehandler();
    }
}
