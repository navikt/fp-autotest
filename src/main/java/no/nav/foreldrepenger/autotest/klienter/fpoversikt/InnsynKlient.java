package no.nav.foreldrepenger.autotest.klienter.fpoversikt;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetBruker;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.fpoversikt.AnnenPartSak;
import no.nav.foreldrepenger.kontrakter.fpoversikt.Saker;

public class InnsynKlient {

    private static final String API_SAKER_PATH = "/api/saker";
    private static final String API_ANNENPARTS_VEDTAK_PATH = "/api/annenPart";

    public Saker hentSaker(Fødselsnummer fnr) {
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FPOVERSIKT_BASE)
                        .path(API_SAKER_PATH)
                        .build())
                .timeout(Duration.ofSeconds(10))
                .GET();
        return send(request.build(), Saker.class);
    }

    public AnnenPartSak hentAnnenpartsSak(Fødselsnummer fnr, AnnenPartSakIdentifikator identifikator) {
        var request = requestMedInnloggetBruker(fnr)
                .uri(fromUri(BaseUriProvider.FPOVERSIKT_BASE)
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
