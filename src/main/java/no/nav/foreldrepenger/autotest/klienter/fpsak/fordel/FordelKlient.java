package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostKnyttningDto;
import no.nav.foreldrepenger.kontrakter.fordel.JournalpostMottakDto;
import no.nav.foreldrepenger.kontrakter.fordel.OpprettSakDto;
import no.nav.foreldrepenger.kontrakter.fordel.SaksnummerDto;

public class FordelKlient {
    private static final String API_NAME = "fpsak";
    private static final String FORDEL_URL = "/fordel";
    private static final String JOURNALPOST_URL = FORDEL_URL + "/journalpost";
    private static final String FAGSAK_URL = FORDEL_URL + "/fagsak";
    private static final String FAGSAK_OPPRETT_URL = FAGSAK_URL + "/opprett";
    private static final String FAGSAK_KNYTT_JOURNALPOST_URL = FAGSAK_URL + "/knyttJournalpost";

    private final SaksbehandlerRolle saksbehandlerRolle;

    public FordelKlient(SaksbehandlerRolle saksbehandlerRolle) {
        this.saksbehandlerRolle = saksbehandlerRolle;
    }

    public void journalpost(JournalpostMottakDto journalpostMottak) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(JOURNALPOST_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(journalpostMottak)));
        send(request.build());
    }

    public Saksnummer fagsakOpprett(OpprettSakDto opprettSakDto) {
        return tilSaksnummer(opprettFagsak(opprettSakDto));
    }

    private SaksnummerDto opprettFagsak(OpprettSakDto opprettSakDto) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(FAGSAK_OPPRETT_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(opprettSakDto)));
        return send(request.build(), SaksnummerDto.class);
    }

    private Saksnummer tilSaksnummer(SaksnummerDto saksnummer) {
        if (saksnummer != null) {
            return Saksnummer.valueOf(saksnummer.getSaksnummer());
        }
        return null;
    }

    public void fagsakKnyttJournalpost(JournalpostKnyttningDto knyttJournalpost) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(FAGSAK_KNYTT_JOURNALPOST_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(knyttJournalpost)));
        send(request.build());
    }
}
