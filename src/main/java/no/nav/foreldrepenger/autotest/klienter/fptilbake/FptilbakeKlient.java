package no.nav.foreldrepenger.autotest.klienter.fptilbake;

import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.JsonRest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FptilbakeKlient extends JsonRest {

    protected Logger log;

    public FptilbakeKlient(HttpSession session) {
        super(session);
        log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public String hentRestRotUrl() {
        return System.getProperty("autotest.fptilbake.http.routing.api");
    }
}
