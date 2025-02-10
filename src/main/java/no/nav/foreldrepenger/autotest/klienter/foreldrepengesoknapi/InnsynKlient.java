package no.nav.foreldrepenger.autotest.klienter.foreldrepengesoknapi;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.innsyn.AnnenPartSak;
import no.nav.foreldrepenger.common.innsyn.Saker;

import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDate;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

public class InnsynKlient {

    private static final String API_INNSYN_PATH = "/rest/innsyn/v2";
    private static final String API_SAKER_PATH = API_INNSYN_PATH + "/saker";
    private static final String API_ANNENPARTS_VEDTAK_PATH = API_INNSYN_PATH + "/annenPartVedtak";

    public Saker hentSaker(Fødselsnummer fnr) {
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FORELDREPENGESOKNAD_API_BASE)
                        .path(API_SAKER_PATH)
                        .build())
                .timeout(Duration.ofSeconds(10))
                .GET();
        return send(request.build(), Saker.class);
    }

    public AnnenPartSak hentAnnenpartsSak(Fødselsnummer fnr, AnnenPartSakIdentifikator identifikator) {
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FORELDREPENGESOKNAD_API_BASE)
                        .path(API_ANNENPARTS_VEDTAK_PATH)
                        .build())
                .timeout(Duration.ofSeconds(10))
                .POST(HttpRequest.BodyPublishers.ofString(toJson(identifikator)));
        return send(request.build(), AnnenPartSak.class);
    }

    public record AnnenPartSakIdentifikator(Fødselsnummer annenPartFødselsnummer,
                                     Fødselsnummer barnFødselsnummer,
                                     LocalDate familiehendelse) {
    }
}
