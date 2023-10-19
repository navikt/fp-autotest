package no.nav.foreldrepenger.autotest.klienter.foreldrepengesoknapi;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBrukerIdporten;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.time.Duration;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Kvittering;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.SøknadDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.endringssøknad.EndringssøknadDto;

public class MottakKlient {

    private static final String API_SEND_PATH = "/rest/soknad";
    private static final String API_ENDRING_PATH = API_SEND_PATH + "/endre";

    public Kvittering sendSøknad(Fødselsnummer fnr, SøknadDto søknad) {
        var request = requestMedInnloggetBrukerIdporten(fnr)
                .uri(fromUri(BaseUriProvider.FORELDREPENGESOKNAD_API_BASE)
                        .path(API_SEND_PATH)
                        .build())
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(toJson(søknad)));
        return send(request.build(), Kvittering.class);
    }

    public Kvittering sendSøknad(Fødselsnummer fnr, EndringssøknadDto søknad) {
        var request = requestMedInnloggetBrukerIdporten(fnr)
                .uri(fromUri(BaseUriProvider.FORELDREPENGESOKNAD_API_BASE)
                        .path(API_ENDRING_PATH)
                        .build())
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(toJson(søknad)));
        return send(request.build(), Kvittering.class);
    }

}
