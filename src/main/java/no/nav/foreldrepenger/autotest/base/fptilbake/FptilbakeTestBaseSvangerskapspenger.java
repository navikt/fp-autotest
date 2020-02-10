package no.nav.foreldrepenger.autotest.base.fptilbake;

import no.nav.foreldrepenger.autotest.aktoerer.fptilbake.TilbakekrevingSaksbehandler;
import no.nav.foreldrepenger.autotest.base.SvangerskapspengerTestBase;
import org.junit.jupiter.api.BeforeEach;

public class FptilbakeTestBaseSvangerskapspenger extends SvangerskapspengerTestBase {

    protected TilbakekrevingSaksbehandler tbksaksbehandler;

    @BeforeEach
    public void setUpTbkSaksbehandlerSVP(){
        tbksaksbehandler = new TilbakekrevingSaksbehandler();
    }
}
