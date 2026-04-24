package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.maskinporten;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.TokenResponse;

public final class MaskinportenKlient {

    private static final String TEXAS_TOKEN_ENDPOINT = "/rest/texas/api/v1/token";
    private static final String IDENTITY_PROVIDER = "maskinporten";

    private static final Map<String, String> accessTokens = new ConcurrentHashMap<>();

    private MaskinportenKlient() {
        // Statisk implementasjon
    }

    public static String hentAccessToken(String scope) {
        return hentAccessToken(scope, null);
    }

    public static String hentAccessToken(String scope, List<AuthorizationDetails> authorizationDetails) {
        return accessTokens.computeIfAbsent(scope, s -> hentMaskinportenToken(s, authorizationDetails));
    }

    private static String hentMaskinportenToken(String scope, List<AuthorizationDetails> authorizationDetails) {
        var texasRequest = new TexasTokenRequest(IDENTITY_PROVIDER, scope, authorizationDetails);
        var request = HttpRequest.newBuilder()
                .uri(fromUri(BaseUriProvider.VTP_ROOT).path(TEXAS_TOKEN_ENDPOINT).build())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .POST(HttpRequest.BodyPublishers.ofString(toJson(texasRequest)))
                .build();
        return send(request, TokenResponse.class).access_token();
    }
}
