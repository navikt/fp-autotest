package no.nav.foreldrepenger.autotest.klienter.foreldrepengesoknapi;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.time.Duration;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.Saker;

public class InnsynKlient {

    private static final String API_INNSYN_PATH = "/rest/innsyn/v2";
    private static final String API_SAKER_PATH = API_INNSYN_PATH + "/saker";

    public Saker hentSaker(Fødselsnummer fnr) {
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FORELDREPENGESOKNAD_API_BASE)
                        .path(API_SAKER_PATH)
                        .build())
                .timeout(Duration.ofSeconds(30))
                .GET();
        return send(request.build(), Saker.class);
    }
}
