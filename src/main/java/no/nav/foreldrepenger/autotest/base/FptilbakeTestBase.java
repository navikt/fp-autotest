package no.nav.foreldrepenger.autotest.base;

import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fptilbake.TilbakekrevingSaksbehandler;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;

// TODO: Fiks opp i testbasene
public class FptilbakeTestBase extends VerdikjedeTestBase {

    protected TilbakekrevingSaksbehandler tbksaksbehandler;
    protected TilbakekrevingSaksbehandler tbkbeslutter;
    protected TilbakekrevingSaksbehandler tbkdrifter;

    @BeforeEach
    public void setUpTbkSaksbehandler() {
        tbksaksbehandler = new TilbakekrevingSaksbehandler(SaksbehandlerRolle.SAKSBEHANDLER);
        tbkbeslutter = new TilbakekrevingSaksbehandler(SaksbehandlerRolle.BESLUTTER);
        tbkdrifter = new TilbakekrevingSaksbehandler(SaksbehandlerRolle.DRIFTER);
    }
}
