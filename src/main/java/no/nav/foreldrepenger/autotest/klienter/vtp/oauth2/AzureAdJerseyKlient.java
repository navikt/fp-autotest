package no.nav.foreldrepenger.autotest.klienter.vtp.oauth2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedHashMap;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.openam.dto.AccessTokenResponseDTO;

public class AzureAdJerseyKlient extends VTPJerseyKlient {

    private static final Map<String, String> tokens = new ConcurrentHashMap<>();

    private static final String AZURE_AD = "/rest/AzureAd";
    private static final String TOKEN_ENDPOINT = AZURE_AD + "/aadb2c/oauth2/v2.0/token";

    public AzureAdJerseyKlient() {
        super();
    }

    public String hentAccessTokenForBruker(String fnr) {
        return tokens.computeIfAbsent(fnr, this::hentAccessTokenFraVtp);
    }

    public String hentAccessTokenFraVtp(String fnr) {
        return client.target(BaseUriProvider.VTP_ROOT)
                .path(TOKEN_ENDPOINT)
                .request()
                .post(Entity.form(new MultivaluedHashMap<>(Map.of(
                        "grant_type", "client_credentials",
                        "scope", "openid",
                        "code", fnr))),
                        AccessTokenResponseDTO.class)
                .getIdToken();
    }
}