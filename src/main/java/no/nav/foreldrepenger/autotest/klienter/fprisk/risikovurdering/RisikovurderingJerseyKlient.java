package no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering;

import static jakarta.ws.rs.client.Entity.json;

import java.util.UUID;

import jakarta.ws.rs.client.ClientRequestFilter;
import no.nav.foreldrepenger.autotest.klienter.fprisk.FpriskJerseyKlient;
import no.nav.foreldrepenger.kontrakter.risk.v1.HentRisikovurderingDto;
import no.nav.foreldrepenger.kontrakter.risk.v1.RisikovurderingResultatDto;

public class RisikovurderingJerseyKlient extends FpriskJerseyKlient {

    private static final String RISIKOVURDERING_HENT_URL = "/risikovurdering/hentResultat";

    public RisikovurderingJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }

    public RisikovurderingResultatDto getRisikovurdering(UUID uuid) {
        return client.target(base)
                .path(RISIKOVURDERING_HENT_URL)
                .request()
                .post(json(new HentRisikovurderingDto(uuid)), RisikovurderingResultatDto.class);
    }
}
