package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure;

import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.TokenResponse;

import java.net.http.HttpRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestBodyPublishers.buildAuthQueryFromMap;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestBodyPublishers.buildFormDataFromMap;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

/**
 * Denne klassen skal tilby følgende token:
 * - Loginservice token som er veksles inn til et TokenX token for en gitt bruker (fnr)
 */
public final class AzureTokenProvider {
    private static final String AZURE_TOKEN_BASE_PATH = "/rest/azuread/token";

    private static final Map<SaksbehandlerRolle, String> saksbehandlerAzureAuthToken = new ConcurrentHashMap<>();
    private static final Map<String, String> saksbehandlerAzureOboToken = new ConcurrentHashMap<>();

    private AzureTokenProvider() {
        // Skal ikke instansieres
    }

    public static String azureOboToken(SaksbehandlerRolle saksbehandlerRolle) {
        String sbhAccessToken = saksbehandlerAzureAuthToken.computeIfAbsent(saksbehandlerRolle,
                token -> saksbehandlerLogin(saksbehandlerRolle));
        return saksbehandlerAzureOboToken.computeIfAbsent(sbhAccessToken, token -> hentOboToken(sbhAccessToken));
    }

    private static String saksbehandlerLogin(SaksbehandlerRolle saksbehandlerRolle) {
        var requestAuth = getAzureRequestBuilder().POST(buildFormDataFromMap(buildAuthQueryFromMap(
                (Map.of("grant_type", "authorization_code",
                        "code", saksbehandlerRolle.getKode(),
                        "client_id", "autotest",
                        "scope", "api://vtp.teamforeldrepenger.vtp/.default")))));

        return send(requestAuth.build(), TokenResponse.class).access_token();
    }

    private static String hentOboToken(String saksbehandlerAccessToken) {
        var requestAuth = getAzureRequestBuilder().POST(buildFormDataFromMap(buildAuthQueryFromMap(
                (Map.of("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer",
                        "client_assertion_type","urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
                        "requested_token_use", "on_behalf_of",
                        "client_id", "autotest",
                        "scope","api://vtp.teamforeldrepenger.vtp/.default", // Forenkling alle clienter i VTP har azure_client_id satt til VTP
                        "assertion", saksbehandlerAccessToken)))));

        return send(requestAuth.build(), TokenResponse.class).access_token();
    }

    private static HttpRequest.Builder getAzureRequestBuilder() {
        return HttpRequest.newBuilder()
                .uri(fromUri(BaseUriProvider.VTP_ROOT).path(AZURE_TOKEN_BASE_PATH).build())
                .header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED);
    }

}
