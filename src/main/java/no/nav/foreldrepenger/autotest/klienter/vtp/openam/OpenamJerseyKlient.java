package no.nav.foreldrepenger.autotest.klienter.vtp.openam;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.openam.dto.AccessTokenResponseDTO;
import no.nav.foreldrepenger.autotest.util.rest.OpenAmRequestFilter;

public class OpenamJerseyKlient extends VTPJerseyKlient {

    private static final String ACCESS_TOKEN_PATH = "/rest/isso/oauth2/access_token";

    private static final Map<String, Cookie> loginCookies = new ConcurrentHashMap<>();

    public OpenamJerseyKlient() {
        super();
    }

    public void logInnMedRolle(String rolle) {
        loginBypass(rolle);
    }

    private void loginBypass(String rolle) {
        var cookie = loginCookies.computeIfAbsent(rolle, this::createCookieNew);
        OpenAmRequestFilter.getInstance().leggTilClientCookie(cookie);
    }

    private Cookie createCookieNew(String rolle) {
        var token = fetchToken(rolle);
        var newCookie = new NewCookie("ID_token", token, "/", "", "", 3600, false);
        return newCookie.toCookie();
    }

    String fetchToken(String rolle) {
        var response = client.target(BaseUriProvider.VTP_ROOT)
                .path(ACCESS_TOKEN_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.form(new Form("code", rolle)), AccessTokenResponseDTO.class);
        return response.idToken;
    }

}
