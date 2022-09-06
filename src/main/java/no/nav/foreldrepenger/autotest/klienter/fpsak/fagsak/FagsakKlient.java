package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Sok;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class FagsakKlient {

    private static final String FAGSAK_URL = "/fagsak";
    private static final String FAGSAK_SØK_URL = FAGSAK_URL + "/sok";

    public Fagsak hentFagsak(Saksnummer saksnummer) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(FAGSAK_URL)
                        .queryParam("saksnummer", saksnummer.value())
                        .build())
                .GET();
        return send(request.build(), Fagsak.class);
    }

    public List<Fagsak> søk(Fødselsnummer fnr) {
        return søk(new Sok(fnr.value()));
    }

    public List<Fagsak> søk(String søk) {
        return søk(new Sok(søk));
    }

    private List<Fagsak> søk(Sok søk) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(FAGSAK_SØK_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(søk)));
        return Optional.ofNullable(send(request.build(), new TypeReference<List<Fagsak>>() {}))
                .orElse(List.of());
    }


}
