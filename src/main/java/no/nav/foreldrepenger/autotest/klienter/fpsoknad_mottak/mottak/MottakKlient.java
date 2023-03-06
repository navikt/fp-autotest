package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.time.Duration;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;

public class MottakKlient {

    private static final String MOTTAK_PATH= "/mottak";
    private static final String MOTTAK_SEND_PATH = MOTTAK_PATH + "/send";
    private static final String MOTTAK_ENDRE_PATH = MOTTAK_PATH + "/endre";

    public Kvittering sendSøknad(Fødselsnummer fnr, Søknad søknad) {
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FPSOKNAD_MOTTAK_BASE)
                        .path(MOTTAK_SEND_PATH)
                        .build())
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(toJson(søknad)));
        return send(request.build(), Kvittering.class);
    }

    public Kvittering sendSøknad(Fødselsnummer fnr, Endringssøknad søknad) {
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FPSOKNAD_MOTTAK_BASE)
                        .path(MOTTAK_ENDRE_PATH)
                        .build())
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(toJson(søknad)));
        return send(request.build(), Kvittering.class);
    }
}
