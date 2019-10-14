package no.nav.foreldrepenger.autotest.klienter.vtp;

import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.JsonRest;

public class VTPKlient extends JsonRest {

    public VTPKlient(HttpSession session) {
        super(session);
    }

    @Override
    public String hentRestRotUrl() {
        return hentRotUrl() + "/rest/api";
    }

    public String hentRotUrl() {
        if (null != System.getenv("AUTOTEST_VTP_BASE_URL")) {
            return System.getenv("AUTOTEST_VTP_BASE_URL");
        } else {
            return System.getProperty("autotest.vtp.url") + ":" + System.getProperty("autotest.vtp.port");
        }
    }

}
