package no.nav.foreldrepenger.autotest.klienter.vtp.openam;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.http.impl.cookie.BasicClientCookie;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.openam.dto.AccessTokenResponseDTO;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenamKlient extends VTPKlient {

    private static final Map<String, BasicClientCookie> loginCookies = new ConcurrentHashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(OpenamKlient.class);

    public OpenamKlient(HttpSession session) {
        super(session);
    }

    public void logInnMedRolle(String rolle) {
        loginBypass(rolle);
    }

    String fetchToken(String rolle) throws IOException {
        String url = hentRotUrl() + "/rest/isso/oauth2/access_token";
        Map<String, String> formData = Map.of("code", rolle);
        AccessTokenResponseDTO result = postFormOgHentJson(url, formData, AccessTokenResponseDTO.class);
        return result.idToken;
    }

    private void loginBypass(String rolle) {
        BasicClientCookie cookie = loginCookies.computeIfAbsent(rolle, this::createCookie);
        session.leggTilCookie(cookie);
    }

    BasicClientCookie createCookie(String rolle) {
        String token = "";
        try {
            token = fetchToken(rolle);
        } catch (IOException e) {
            LOG.error("Klarte ikke å hente Token fra VTP feilmedling: " + e.getMessage());
        }
        BasicClientCookie cookie = new BasicClientCookie("ID_token", token);
        cookie.setPath("/");
        cookie.setDomain("");
        cookie.setExpiryDate(new Date(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
        return cookie;
    }


}
