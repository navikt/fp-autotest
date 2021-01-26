package no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering;

import static javax.ws.rs.client.Entity.json;

import no.nav.foreldrepenger.autotest.klienter.fprisk.FpriskJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.dto.RisikovurderingRequest;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.dto.RisikovurderingResponse;

public class RisikovurderingJerseyKlient extends FpriskJerseyKlient {

    private static final String RISIKOVURDERING_URL = "/risikovurdering";
    private static final String RISIKOVURDERING_HENT_URL = RISIKOVURDERING_URL + "/hent";

    public RisikovurderingJerseyKlient() {
        super();
    }

    public RisikovurderingResponse getRisikovurdering(String uuid) {
        return client.target(base)
                .path(RISIKOVURDERING_HENT_URL)
                .request()
                .post(json(new RisikovurderingRequest(uuid)), RisikovurderingResponse.class);
    }
}
