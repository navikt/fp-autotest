package no.nav.foreldrepenger.autotest.klienter.vtp.oauth2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MultivaluedHashMap;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.modell.Fødselsnummer;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.vtp.openam.dto.AccessTokenResponseDTO;

public class AzureAdJerseyKlient extends VTPJerseyKlient {

    private static final Map<Fødselsnummer, String> tokens = new ConcurrentHashMap<>();

    private static final String AZURE_AD = "/rest/AzureAd";
    private static final String TOKEN_ENDPOINT = AZURE_AD + "/aadb2c/oauth2/v2.0/token";

    public AzureAdJerseyKlient() {
        super();
    }

    public String hentAccessTokenForBruker(Fødselsnummer fnr) {
        return tokens.computeIfAbsent(fnr, this::hentAccessTokenFraVtp);
    }

    public String hentAccessTokenFraVtp(Fødselsnummer fnr) {
        return client.target(BaseUriProvider.VTP_ROOT)
                .path(TOKEN_ENDPOINT)
                .request()
                .post(Entity.form(new MultivaluedHashMap<>(Map.of(
                        "grant_type", "client_credentials",
                        "scope", "openid",
                        "code", fnr.toString()))),
                        AccessTokenResponseDTO.class)
                .getIdToken();
    }
}
