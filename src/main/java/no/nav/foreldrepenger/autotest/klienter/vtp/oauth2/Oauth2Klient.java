package no.nav.foreldrepenger.autotest.klienter.vtp.oauth2;

import org.apache.http.HttpResponse;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;

public class Oauth2Klient extends VTPKlient {

    private static final String OAUTH2_PATH = "/oauth2";
    private static final String HENT_TOKEN = OAUTH2_PATH + "?fnr=%s";

    public Oauth2Klient(HttpSession session) {
        super(session);
    }

    public String hentTokenForFnr(String fnr) {
        var url = String.format(hentRestRotUrl() + HENT_TOKEN, fnr);
        HttpResponse response = get(url);
        return HttpSession.readRawResponse(response);
    }
}
