package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.EndreUtlandMarkering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Sok;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class FagsakKlient {

    private static final String API_NAME = "fpsak";

    private static final String FAGSAK_URL = "/fagsak";
    private static final String FAGSAK_FULL_URL = FAGSAK_URL + "/full";
    private static final String FAGSAK_SØK_URL = FAGSAK_URL + "/sok";
    private static final String ENDRE_FAGSAK_MARKERING = FAGSAK_URL + "/endre-utland";

    private final SaksbehandlerRolle saksbehandlerRolle;

    public FagsakKlient(SaksbehandlerRolle saksbehandlerRolle) {
        this.saksbehandlerRolle = saksbehandlerRolle;
    }

    public Fagsak hentFagsak(Saksnummer saksnummer) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(FAGSAK_FULL_URL)
                        .queryParam("saksnummer", saksnummer.value())
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), Fagsak.class))
                .orElseThrow(() -> new RuntimeException("Finner ikke fagsak på saksnummer " + saksnummer));
    }

    public List<Fagsak> søk(Fødselsnummer fnr) {
        return søk(new Sok(fnr.value()));
    }

    public void endreFagsakMarkering(EndreUtlandMarkering endreUtlandMarkering) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(ENDRE_FAGSAK_MARKERING)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(endreUtlandMarkering)));
        send(request.build());
    }

    private List<Fagsak> søk(Sok søk) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, API_NAME)
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(FAGSAK_SØK_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(søk)));
        return Optional.ofNullable(send(request.build(), new TypeReference<List<Fagsak>>() {}))
                .orElse(List.of());
    }

}
