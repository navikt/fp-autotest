package no.nav.foreldrepenger.autotest.klienter.vtp.openam;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.impl.cookie.BasicClientCookie;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.openam.dto.AccessTokenResponseDTO;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;

public class OpenamKlient extends VTPKlient {

    private static final Map<String, BasicClientCookie> loginCookies = new ConcurrentHashMap<>();

    public OpenamKlient(HttpSession session) {
        super(session);
    }

    public void logInnMedRolle(String rolle) {
        loginBypass(rolle);
    }

    String fetchToken(String rolle) {
        String url = hentRotUrl() + "/rest/isso/oauth2/access_token";
        Map<String, String> formData = Map.of("code", rolle,
                "test","test");
        AccessTokenResponseDTO result = postFormOgHentJson(url, formData, AccessTokenResponseDTO.class);
        return result.idToken;
    }

    private void loginBypass(String rolle) {
        BasicClientCookie cookie = loginCookies.computeIfAbsent(rolle, this::createCookie);
        session.leggTilCookie(cookie);
    }

    BasicClientCookie createCookie(String rolle) {
        String token = fetchToken(rolle);
        BasicClientCookie cookie = new BasicClientCookie("ID_token", token);
        cookie.setPath("/");
        cookie.setDomain("");
        cookie.setExpiryDate(
                new Date(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        return cookie;
    }

}
