package no.nav.foreldrepenger.autotest.klienter.vtp.oauth2;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.http.HttpResponse;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.openam.dto.AccessTokenResponseDTO;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;

public class Oauth2Klient extends VTPKlient {

    private static final String OAUTH2_PATH = "/oauth2";
    private static final String HENT_TOKEN = OAUTH2_PATH + "?fnr=%s";

    private static final String OPENAM = "/rest/isso";
    private static final String PATH = OPENAM + "/oauth2/access_token";


    public Oauth2Klient(HttpSession session) {
        super(session);
    }

    public String hentTokenForFnr(String fnr) {
        var url = String.format(hentRestRotUrl() + HENT_TOKEN, fnr);
        HttpResponse response = get(url);
        return HttpSession.readRawResponse(response);
    }

    public AccessTokenResponseDTO hentTokenVtpForFnr(String fnr) throws UnsupportedEncodingException {
        var url = hentRotUrl() + PATH;
//        var params = List.of(new BasicNameValuePair("grant_type", "client_credentials"),
//                new BasicNameValuePair("scope", "openid"),
//                new BasicNameValuePair("code", fnr));
        var params = Map.of("grant_type", "client_credentials",
                "scope", "openid",
                "code", fnr);
        return postFormOgHentJson(url, params, AccessTokenResponseDTO.class);
    }
}
