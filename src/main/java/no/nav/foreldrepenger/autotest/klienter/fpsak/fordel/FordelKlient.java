package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.send;

import java.net.http.HttpRequest;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostKnyttningDto;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostMottakDto;
import no.nav.foreldrepenger.kontrakter.fordel.OpprettSakDto;
import no.nav.foreldrepenger.kontrakter.fordel.SaksnummerDto;

public class FordelKlient {

    private static final String FORDEL_URL = "/fordel";
    private static final String JOURNALPOST_URL = FORDEL_URL + "/journalpost";

    private static final String FAGSAK_URL = FORDEL_URL + "/fagsak";
    private static final String FAGSAK_OPPRETT_URL = FAGSAK_URL + "/opprett";
    private static final String FAGSAK_KNYTT_JOURNALPOST_URL = FAGSAK_URL + "/knyttJournalpost";

    public void journalpost(JournalpostMottakDto journalpostMottak) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSAK_BASE).path(JOURNALPOST_URL).build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(journalpostMottak)));
        send(request.build());
    }

    public Saksnummer fagsakOpprett(OpprettSakDto opprettSakDto) {
        return tilSaksnummer(opprettFagsak(opprettSakDto));
    }

    private SaksnummerDto opprettFagsak(OpprettSakDto opprettSakDto) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(FAGSAK_OPPRETT_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(opprettSakDto)));
        return send(request.build(), SaksnummerDto.class);
    }

    private Saksnummer tilSaksnummer(SaksnummerDto saksnummer) {
        if (saksnummer != null) {
            return Saksnummer.valueOf(saksnummer.getSaksnummer());
        }
        return null;
    }

    public void fagsakKnyttJournalpost(JournalpostKnyttningDto knyttJournalpost) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(FAGSAK_KNYTT_JOURNALPOST_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(knyttJournalpost)));
        send(request.build());
    }
}
