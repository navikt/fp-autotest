package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.tokenx;

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

public class TokenXVekslingKlient {

    private static final Map<Fødselsnummer, String> subjectTokens = new ConcurrentHashMap<>();
    private static final Map<Fødselsnummer, String> accessTokens = new ConcurrentHashMap<>();

    private static final String IDPORTEN_TOKEN_ENDPOINT = "/rest/idporten/bruker";
    private static final String TOKENX_TOKEN_ENDPOINT = "/rest/tokenx/token";

    private TokenXVekslingKlient() {
        // Statisk implementasjon
    }

    public static String hentAccessTokenForBruker(Fødselsnummer fnr) {
        return accessTokens.computeIfAbsent(fnr, TokenXVekslingKlient::hentBrukerAccessToken);
    }

    private static String hentBrukerAccessToken(Fødselsnummer fnr) {
        var subjectToken = subjectTokens.computeIfAbsent(fnr, TokenXVekslingKlient::hentSubjectTokenFraIdporten);
        return vekslerInnSubjectTokenForEtAccessTokenFraTokenX(subjectToken);
    }

    private static String hentSubjectTokenFraIdporten(Fødselsnummer fnr) {
        var request = HttpRequest.newBuilder()
                .uri(fromUri(BaseUriProvider.VTP_ROOT)
                        .path(IDPORTEN_TOKEN_ENDPOINT)
                        .queryParam("fnr", fnr.value())
                        .build())
                .header(CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .GET();
        var tokenResponse = send(request.build(), TokenResponse.class);
        return tokenResponse.id_token();
    }

    private static String vekslerInnSubjectTokenForEtAccessTokenFraTokenX(String subjectToken) {
        var request = HttpRequest.newBuilder()
                .uri(fromUri(BaseUriProvider.VTP_ROOT)
                        .path(TOKENX_TOKEN_ENDPOINT)
                        .build())
                .header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .POST(buildFormDataFromMap(buildAuthQueryFromMap(Map.of(
                        "subject_token", subjectToken,
                        "audience", "lokal"))));
        var tokenResponse = send(request.build(), TokenResponse.class);
        return tokenResponse.access_token();
    }
}
