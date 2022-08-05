package no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.send;

import java.net.http.HttpRequest;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.foreldrepenger.kontrakter.risk.v1.HentRisikovurderingDto;
import no.nav.foreldrepenger.kontrakter.risk.v1.RisikovurderingResultatDto;

public class RisikovurderingKlient {

    private static final String RISIKOVURDERING_URL = "/risikovurdering";
    private static final String RISIKOVURDERING_HENT_URL = RISIKOVURDERING_URL + "/hentResultat";

    public RisikovurderingResultatDto getRisikovurdering(UUID uuid) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPRISK_BASE)
                        .path(RISIKOVURDERING_HENT_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(new HentRisikovurderingDto(uuid))));
        return send(request.build(), RisikovurderingResultatDto.class);
    }
}
