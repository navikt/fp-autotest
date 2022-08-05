package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.send;

import java.net.http.HttpRequest;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.tokenx.TokenXHenterKlient;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;

public class MottakKlient {

    private static final TokenXHenterKlient tokenXHenterKlient = new TokenXHenterKlient();

    public static final String OIDC_AUTH_HEADER_PREFIX = "Bearer ";
    private static final String MOTTAK_PATH= "/mottak";
    private static final String MOTTAK_SEND_PATH = MOTTAK_PATH + "/send";
    private static final String MOTTAK_ENDRE_PATH = MOTTAK_PATH + "/endre";


    public Kvittering sendSøknad(Fødselsnummer fnr, Søknad søknad) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSOKNAD_MOTTAK_BASE)
                        .path(MOTTAK_SEND_PATH)
                        .build())
                .header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + fetchToken(fnr)) // TODO: Bedre måte å gjøre det på? Felles cookie vs header?
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(søknad)));
        return send(request.build(), Kvittering.class);
    }

    public Kvittering sendSøknad(Fødselsnummer fnr, Endringssøknad søknad) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSOKNAD_MOTTAK_BASE)
                        .path(MOTTAK_ENDRE_PATH)
                        .build())
                .header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + fetchToken(fnr))
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(søknad)));
        return send(request.build(), Kvittering.class);
    }

    private String fetchToken(Fødselsnummer fnr) {
        return tokenXHenterKlient.hentAccessTokenForBruker(fnr);
    }
}
