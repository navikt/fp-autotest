package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.tokenx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MultivaluedHashMap;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

public class TokenXHenterKlient extends VTPJerseyKlient {

    private static final Map<Fødselsnummer, String> subjectTokens = new ConcurrentHashMap<>();
    private static final Map<Fødselsnummer, String> accessTokens = new ConcurrentHashMap<>();

    private static final String TOKEN_ENDPOINT_AZURE_AD = "/rest/AzureAd/loginservice/oauth2/v2.0/token";
    private static final String TOKEN_ENDPOINT_TOKENX = "/rest/tokenx/token";

    public TokenXHenterKlient() {
        super();
    }

    public String hentAccessTokenForBruker(Fødselsnummer fnr) {
        return accessTokens.computeIfAbsent(fnr, this::hentAccessTokenFraVtp);
    }

    private String hentAccessTokenFraVtp(Fødselsnummer fnr) {
        var subjectToken = subjectTokens.computeIfAbsent(fnr, this::hentSubjectTokenFraLoginserviceVtp);
        return vekslerInnSubjectTokenForEtAccessTokenFraTokenDings(subjectToken);
    }

    private String hentSubjectTokenFraLoginserviceVtp(Fødselsnummer fnr) {
        return client.target(BaseUriProvider.VTP_ROOT)
                .path(TOKEN_ENDPOINT_AZURE_AD)
                .request()
                .post(Entity.form(new MultivaluedHashMap<>(
                                Map.of("grant_type", "client_credentials",
                                        "scope", "openid",
                                        "code", fnr.value()))),
                        TokenResponse.class)
                .id_token();
    }

    private String vekslerInnSubjectTokenForEtAccessTokenFraTokenDings(String subjectToken) {
        return client.target(BaseUriProvider.VTP_ROOT)
                .path(TOKEN_ENDPOINT_TOKENX)
                .request()
                .post(Entity.form(new MultivaluedHashMap<>(
                                Map.of("subject_token", subjectToken,
                                        "audience", "lokal"))),
                        TokenResponse.class)
                .access_token();
    }
}
