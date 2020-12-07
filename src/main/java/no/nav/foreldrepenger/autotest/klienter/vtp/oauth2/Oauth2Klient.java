package no.nav.foreldrepenger.autotest.klienter.vtp.oauth2;

import java.util.Map;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.openam.dto.AccessTokenResponseDTO;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;

public class Oauth2Klient extends VTPKlient {

    private static final String AZURE_AD = "/rest/AzureAd";
    private static final String TOKEN_ENDPOINT = AZURE_AD + "/aadb2c/oauth2/v2.0/token";


    public Oauth2Klient(HttpSession session) {
        super(session);
    }

    public AccessTokenResponseDTO hentAccessTokenForBruker(String fnr) {
        var url = hentRotUrl() + TOKEN_ENDPOINT;
        var params = Map.of("grant_type", "client_credentials",
                "scope", "openid",
                "code", fnr);
        return postFormOgHentJson(url, params, AccessTokenResponseDTO.class);
    }
}
