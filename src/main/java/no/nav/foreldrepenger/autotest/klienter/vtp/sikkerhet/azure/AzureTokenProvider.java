package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure;

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
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.TokenResponse;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

/**
 * Denne klassen skal tilby følgende token:
 *  - OpenAM Token
 *  - Loginservice token som er veksles inn til et TokenX token for en gitt bruker (fnr)
 */
public final class AzureTokenProvider {
    private static final String AZURE_TOKEN_BASE_PATH = "/azureAd/token";
    private static final String IDPORTEN_TOKEN_BASE_PATH = "/idPorten/token";

    private static final Map<SaksbehandlerRolle, String> saksbehandlerAzureAuthToken = new ConcurrentHashMap<>();
    private static final Map<String, String> saksbehandlerAzureOboToken = new ConcurrentHashMap<>();
    private static final Map<Fødselsnummer, String> accessTokensTokenX = new ConcurrentHashMap<>();

    private AzureTokenProvider() {
        // Skal ikke instansieres
    }

    public static String azureOboToken(SaksbehandlerRolle saksbehandlerRolle, String clientId) {
        String sbhAccessToken = saksbehandlerAzureAuthToken.computeIfAbsent(saksbehandlerRolle, token -> saksbehandlerLogin(saksbehandlerRolle));
        return saksbehandlerAzureOboToken
                .computeIfAbsent(String.format("%s-%s", sbhAccessToken, clientId), token -> hentOboToken(sbhAccessToken, clientId));
    }

    public static String tokenXToken(Fødselsnummer fnr, String audience) { // Vi bruker det bare til å kalle fpsoknad-mottak ATM
        return accessTokensTokenX.computeIfAbsent(fnr, ident -> hentNyttTokenXTokenFor(ident, audience));
    }

    private static String saksbehandlerLogin(SaksbehandlerRolle saksbehandlerRolle) {
        var requestAuth =  getAzureRequestBuilder()
                .POST(buildFormDataFromMap(buildAuthQueryFromMap((Map.of(
                        "grant_type", "authorization_code",
                        "code", saksbehandlerRolle.getKode(),
                        "client_id", "autotest",
                        "scope", "api://vtp.teamforeldrepenger.fpfrontend/.default")))
                ));

        return send(requestAuth.build(), TokenResponse.class).access_token();
    }

    private static String hentOboToken(String saksbehandlerAccessToken, String clientId) {
        var requestAuth =  getAzureRequestBuilder()
                .POST(buildFormDataFromMap(buildAuthQueryFromMap((Map.of(
                        "grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer",
                        "client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
                        "requested_token_use", "on_behalf_of",
                        "scope", String.format("api://vtp.teamforeldrepenger.%s/.default", clientId),
                        "assertion", saksbehandlerAccessToken)))
                ));

        return send(requestAuth.build(), TokenResponse.class).access_token();
    }

    private static HttpRequest.Builder getAzureRequestBuilder() {
        return HttpRequest.newBuilder()
                .uri(fromUri(BaseUriProvider.AZURE_ROOT)
                        .path(AZURE_TOKEN_BASE_PATH)
                        .build())
                .header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .header("Host", "authserver:8086");
    }

    private static String hentNyttTokenXTokenFor(Fødselsnummer fnr, String audience) {
        var request =  HttpRequest.newBuilder()
                .uri(fromUri(BaseUriProvider.AZURE_ROOT)
                        .path(IDPORTEN_TOKEN_BASE_PATH)
                        .build())
                .header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .POST(buildFormDataFromMap(buildAuthQueryFromMap(Map.of(
                        "audience", audience, // TODO
                        "grant_type", "authorization_code",
                        "client_id", "lokal",
                        "fnr", fnr.value(),
                        "code", "some_random_code"))
                ));
        return send(request.build(), TokenResponse.class).access_token();
    }

}
