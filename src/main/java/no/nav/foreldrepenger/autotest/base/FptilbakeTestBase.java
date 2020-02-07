package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.aktoerer.fptilbake.TilbakekrevingSaksbehandler;
import org.junit.jupiter.api.BeforeEach;

//TODO: Lage egne TestBaser som utvider de andre ytelses-test-basene
public class FptilbakeTestBase extends ForeldrepengerTestBase {

    protected TilbakekrevingSaksbehandler tbksaksbehandler;

    @BeforeEach
    public void setUp(){
        tbksaksbehandler = new TilbakekrevingSaksbehandler();
    }
}
