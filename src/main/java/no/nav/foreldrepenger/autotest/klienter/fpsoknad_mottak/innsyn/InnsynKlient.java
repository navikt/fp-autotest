package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.FpsoknadMottakJerseyKlient;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;

public class InnsynKlient extends FpsoknadMottakJerseyKlient {

    private static final String INNSYN_V2 = "/innsyn/v2";
    private static final String SAKER_PATH = INNSYN_V2 + "/saker";

    public InnsynKlient() {
        super();
    }

    // TODO: Requestfilter for token istedenfor?
    public Saker hentSaker(String token) {
        return client.target(base)
                .path(SAKER_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + token)
                .get(Saker.class);
    }
}
