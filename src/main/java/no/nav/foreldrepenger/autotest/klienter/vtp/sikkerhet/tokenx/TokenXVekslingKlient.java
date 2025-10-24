package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.tokenx;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.TokenResponse;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Fødselsnummer;

public class TokenXVekslingKlient {

    private static final Map<Fødselsnummer, String> accessTokens = new ConcurrentHashMap<>();

    private static final String TOKENX_TOKEN_ENDPOINT = "/rest/tokenx/token";

    private TokenXVekslingKlient() {
        // Statisk implementasjon
    }

    public static String hentAccessTokenForBruker(Fødselsnummer fnr) {
        return accessTokens.computeIfAbsent(fnr, TokenXVekslingKlient::hentOBOtokenFraToknedingsForBruker);
    }

    private static String hentOBOtokenFraToknedingsForBruker(Fødselsnummer fødselsnummer) {
        var request = HttpRequest.newBuilder()
                .uri(fromUri(BaseUriProvider.VTP_ROOT)
                        .path(TOKENX_TOKEN_ENDPOINT)
                        .queryParam("fnr", fødselsnummer.value())
                        .queryParam("audience", "lokal")
                        .build())
                .GET().build();
        var tokenResponse = send(request, TokenResponse.class);
        return tokenResponse.access_token();
    }
}
