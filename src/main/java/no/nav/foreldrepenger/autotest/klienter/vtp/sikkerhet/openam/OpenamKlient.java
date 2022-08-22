package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestBodyPublishers.buildFormDataFromMap;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.HttpCookie;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam.dto.AccessTokenResponseDTO;

public class OpenamKlient {

    private static final String ACCESS_TOKEN_PATH = "/rest/isso/oauth2/access_token";

    // TODO: legg inn logik på å hente ny hver time, eller sjekk om token har gått ut.
    private static final Map<String, HttpCookie> loginCookies = new ConcurrentHashMap<>();

    public HttpCookie logInnMedRolle(String rolle) {
        return loginCookies.computeIfAbsent(rolle, this::createCookie);
    }

    private HttpCookie createCookie(String rolle) {
        var token = fetchToken(rolle);
        var httpcookie = new HttpCookie("ID_token", token);
        httpcookie.setPath("/");
        httpcookie.setDomain("127.0.0.1");
        return httpcookie;
    }

    String fetchToken(String rolle) {
        var request =  HttpRequest.newBuilder()
                .uri(fromUri(BaseUriProvider.VTP_ROOT)
                        .path(ACCESS_TOKEN_PATH)
                        .build())
                .header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .POST(buildFormDataFromMap(Map.of("code", rolle)));
        var accessTokenResponseDTO = send(request.build(), AccessTokenResponseDTO.class);
        return accessTokenResponseDTO.getIdToken();
    }
}
