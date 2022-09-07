package no.nav.foreldrepenger.autotest.base;

import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fptilbake.TilbakekrevingSaksbehandler;

// TODO: Fiks opp i testbasene
public class FptilbakeTestBase extends VerdikjedeTestBase {

    protected TilbakekrevingSaksbehandler tbksaksbehandler;
    protected TilbakekrevingSaksbehandler tbkbeslutter;

    @BeforeEach
    public void setUpTbkSaksbehandler() {
        tbksaksbehandler = new TilbakekrevingSaksbehandler(Aktoer.Rolle.SAKSBEHANDLER);
        tbkbeslutter = new TilbakekrevingSaksbehandler(Aktoer.Rolle.BESLUTTER);
    }
}
