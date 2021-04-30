package no.nav.foreldrepenger.autotest.klienter.vtp.openam;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.openam.dto.AccessTokenResponseDTO;

public class OpenamJerseyKlient extends VTPJerseyKlient {

    private static final String ACCESS_TOKEN_PATH = "/rest/isso/oauth2/access_token";

    private static final Map<String, Cookie> loginCookies = new ConcurrentHashMap<>();

    public OpenamJerseyKlient() {
        super();
    }

    public Cookie logInnMedRolle(String rolle) {
        return loginCookies.computeIfAbsent(rolle, this::createCookieNew);
    }

    private Cookie createCookieNew(String rolle) {
        var token = fetchToken(rolle);
        var newCookie = new NewCookie("ID_token", token, "/", "", "", 60, false);
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
