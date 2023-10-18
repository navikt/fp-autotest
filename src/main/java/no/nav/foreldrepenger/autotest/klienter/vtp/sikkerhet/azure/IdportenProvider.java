package no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static java.net.http.HttpRequest.newBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.TokenResponse;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

public final class IdportenProvider {
    private static final String IDPORTEN_GET_TOKEN_PATH = "/rest/idporten/bruker";

    private IdportenProvider() {
        // Skal ikke instansieres
    }

    public static String idportenToken(Fødselsnummer fnr) {
        var req = newBuilder().uri(
                        fromUri(BaseUriProvider.VTP_ROOT)
                                .path(IDPORTEN_GET_TOKEN_PATH)
                                .queryParam("fnr", fnr.value()).build())
                .header(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED)
                .GET();
        return send(req.build(), TokenResponse.class).access_token();
    }

}
