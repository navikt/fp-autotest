package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn;

import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.MottakKlient.OIDC_AUTH_HEADER_PREFIX;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.tokenx.TokenXVekslingKlient;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;

public class InnsynKlient {

    private static final String INNSYN_V2 = "/innsyn/v2";
    private static final String SAKER_PATH = INNSYN_V2 + "/saker";
    private static final TokenXVekslingKlient tokenxVekslingKlient = new TokenXVekslingKlient();


    // TODO: Requestfilter for token istedenfor?
    public Saker hentSaker(Fødselsnummer fnr) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSOKNAD_MOTTAK_BASE)
                        .path(SAKER_PATH)
                        .build())
                .header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + tokenxVekslingKlient.hentAccessTokenForBruker(fnr))
                .GET();
        return send(request.build(), Saker.class);
    }
}
