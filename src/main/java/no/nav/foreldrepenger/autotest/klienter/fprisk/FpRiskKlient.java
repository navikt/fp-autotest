package no.nav.foreldrepenger.autotest.klienter.fprisk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.JsonRest;

public class FpRiskKlient extends JsonRest {

    protected Logger log;

    public FpRiskKlient(HttpSession session) {
        super(session);
        log = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public String hentRestRotUrl() {
        return System.getProperty("autotest.fprisk.http.routing.api");
    }
}
