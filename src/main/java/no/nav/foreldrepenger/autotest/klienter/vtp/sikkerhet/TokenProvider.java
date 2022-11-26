package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet;

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
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.vedtak.sikkerhet.oidc.token.impl.OidcTokenResponse;

/**
 * Denne klassen skal tilby følgende token:
 *  - OpenAM Token
 *  - Loginservice token som er veksles inn til et TokenX token for en gitt bruker (fnr)
 */
public final class TokenProvider {

    private static final String TOKEN_PROVIDER_BASE_PATH = "/rest/token/provider";
    private static final String TOKENX_ENDPOINT = TOKEN_PROVIDER_BASE_PATH + "/borger/tokenx";
    private static final String AZUREAD_ENDPOINT = TOKEN_PROVIDER_BASE_PATH + "/ansatt/azuread";
    private static final String AZURE_TOKEN_BASE_PATH = "/azureAd/token";
    private static final String IDPORTEN_TOKEN_BASE_PATH = "/idPorten/token";

    private static final Map<SaksbehandlerRolle, Map<String, String>> saksbehandlerTokenAzureAD = new ConcurrentHashMap<>();
    private static final Map<Fødselsnummer, String> accessTokensTokenX = new ConcurrentHashMap<>();

    private TokenProvider() {
        // Skal ikke instansieres
    }

    public static String azureAdToken(SaksbehandlerRolle saksbehandlerRolle, String clientId) {
        return saksbehandlerTokenAzureAD
                .computeIfAbsent(saksbehandlerRolle, rolle -> new ConcurrentHashMap<>(Map.of(clientId, hentNyttAzureAdTokenForSaksbehandler(rolle, clientId))))
                .computeIfAbsent(clientId, id -> hentNyttAzureAdTokenForSaksbehandler(saksbehandlerRolle, id));
    }

    public static String tokenXToken(Fødselsnummer fnr, String audience) { // Vi bruker det bare til å kalle fpsoknad-mottak ATM
        return accessTokensTokenX.computeIfAbsent(fnr, ident -> hentNyttTokenXTokenFor(ident, audience));
    }

    private static String hentNyttAzureAdTokenForSaksbehandler(SaksbehandlerRolle saksbehandlerRolle, String scope) {
        var requestAuth =  HttpRequest.newBuilder()
                .uri(fromUri(BaseUriProvider.AZURE_ROOT)
                        .path(AZURE_TOKEN_BASE_PATH)
                        .build())
                .header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .header("Host", "authserver:8085")
                .POST(buildFormDataFromMap(buildAuthQueryFromMap((Map.of(
                        "code", saksbehandlerRolle.getKode(),
                        "grant_type", "authorization_code",
                        "client_id", "autotest",
                        "scope", "api://fpfrontend/.default")))
                ));

        var accessTokenResponseDTO = send(requestAuth.build(), OidcTokenResponse.class);
        return accessTokenResponseDTO.access_token();
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
        var accessTokenResponseDTO = send(request.build(), OidcTokenResponse.class);
        return accessTokenResponseDTO.access_token();
    }

}
