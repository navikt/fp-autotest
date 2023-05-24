package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.Saker;

public class InnsynKlient {

    private static final String FPOVERSIKT_SAKER = "/saker";

    public Saker hentSaker(Fødselsnummer fnr) {
        var request = byggRequest(fnr, BaseUriProvider.FPOVERSIKT_BASE, FPOVERSIKT_SAKER);
        return send(request.build(), Saker.class);
    }

    private static HttpRequest.Builder byggRequest(Fødselsnummer fnr, URI base, String path) {
        return requestMedInnloggetBruker(fnr).uri(fromUri(base).path(path).build())
                .timeout(Duration.ofSeconds(30))
                .GET();
    }
}
