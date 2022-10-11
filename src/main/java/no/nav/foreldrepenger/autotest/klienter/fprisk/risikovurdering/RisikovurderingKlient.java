package no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.SaksbehandlerRolle;
import no.nav.foreldrepenger.kontrakter.risk.v1.HentRisikovurderingDto;
import no.nav.foreldrepenger.kontrakter.risk.v1.RisikovurderingResultatDto;

public class RisikovurderingKlient {

    private static final String RISIKOVURDERING_URL = "/risikovurdering";
    private static final String RISIKOVURDERING_HENT_URL = RISIKOVURDERING_URL + "/hentResultat";

    private final SaksbehandlerRolle saksbehandlerRolle;

    public RisikovurderingKlient(SaksbehandlerRolle saksbehandlerRolle) {
        this.saksbehandlerRolle = saksbehandlerRolle;
    }

    public RisikovurderingResultatDto getRisikovurdering(UUID uuid) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                .uri(fromUri(BaseUriProvider.FPRISK_BASE)
                        .path(RISIKOVURDERING_HENT_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(new HentRisikovurderingDto(uuid))));
        return send(request.build(), RisikovurderingResultatDto.class);
    }
}
