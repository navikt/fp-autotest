package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestBodyPublishers.buildAuthQueryFromMap;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestBodyPublishers.buildFormDataFromMap;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam.dto.AccessTokenResponseDTO;

public class OpenAMKlient {

    private static final String ACCESS_TOKEN_PATH = "/rest/isso/oauth2/access_token";
    private static final Map<SaksbehandlerRolle, String> saksbehandlerToken = new ConcurrentHashMap<>();

    private OpenAMKlient() {
        // Statisk implementasjon
    }

    public static String logInnMedRolle(SaksbehandlerRolle saksbehandlerRolle) {
        return saksbehandlerToken.computeIfAbsent(saksbehandlerRolle, OpenAMKlient::fetchToken);
    }

    private static String fetchToken(SaksbehandlerRolle saksbehandlerRolle) {
        var request =  HttpRequest.newBuilder()
                .uri(fromUri(BaseUriProvider.VTP_ROOT)
                        .path(ACCESS_TOKEN_PATH)
                        .build())
                .header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .POST(buildFormDataFromMap(buildAuthQueryFromMap(Map.of("code", saksbehandlerRolle.getKode()))));
        var accessTokenResponseDTO = send(request.build(), AccessTokenResponseDTO.class);
        return accessTokenResponseDTO.getIdToken();
    }
}
