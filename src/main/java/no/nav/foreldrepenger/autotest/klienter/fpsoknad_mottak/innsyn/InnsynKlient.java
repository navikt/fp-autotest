package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.MottakKlient.OIDC_AUTH_HEADER_PREFIX;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;

public class InnsynKlient {

    private static final String INNSYN_V2 = "/innsyn/v2";
    private static final String SAKER_PATH = INNSYN_V2 + "/saker";


    // TODO: Requestfilter for token istedenfor?
    public Saker hentSaker(String token) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSOKNAD_MOTTAK_BASE)
                        .path(SAKER_PATH)
                        .build())
                .header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + token)
                .GET();
        return send(request.build(), Saker.class);
    }
}
