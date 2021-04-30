package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak;

import static jakarta.ws.rs.client.Entity.json;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.FpsoknadMottakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.dto.Kvittering;
import no.nav.foreldrepenger.autotest.søknad.modell.Søknad;

public class MottakJerseyKlient extends FpsoknadMottakJerseyKlient {

    private static final String MOTTAK_PATH= "/mottak";
    private static final String MOTTAK_SEND_PATH = MOTTAK_PATH + "/send";

    public MottakJerseyKlient() {
        super();
    }

    public Kvittering sendSøknad(String token, Søknad søknad) {
        return client.target(base)
                .path(MOTTAK_SEND_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + token)
                .post(json(søknad), Kvittering.class);
    }
}
