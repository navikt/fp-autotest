package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.time.Duration;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.v2.Saker;

public class InnsynKlient {

    private static final String INNSYN_V2 = "/innsyn/v2";
    private static final String SAKER_PATH = INNSYN_V2 + "/saker";

    public Saker hentSaker(Fødselsnummer fnr) {
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FPSOKNAD_MOTTAK_BASE)
                        .path(SAKER_PATH)
                        .build())
                .timeout(Duration.ofSeconds(10))
                .GET();
        return send(request.build(), Saker.class);
    }
}
