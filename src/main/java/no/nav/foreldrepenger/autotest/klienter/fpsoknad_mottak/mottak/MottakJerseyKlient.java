package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak;

import static jakarta.ws.rs.client.Entity.json;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;

import jakarta.ws.rs.core.MediaType;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.FpsoknadMottakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.dto.Kvittering;
import no.nav.foreldrepenger.common.domain.Søknad;
import no.nav.foreldrepenger.common.domain.foreldrepenger.Endringssøknad;

public class MottakJerseyKlient extends FpsoknadMottakJerseyKlient {

    private static final String MOTTAK_PATH= "/mottak";
    private static final String MOTTAK_SEND_PATH = MOTTAK_PATH + "/send";
    private static final String MOTTAK_ENDRE_PATH = MOTTAK_PATH + "/endre";

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
    public Kvittering sendSøknad(String token, Endringssøknad søknad) {
        return client.target(base)
                .path(MOTTAK_ENDRE_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header(AUTHORIZATION, OIDC_AUTH_HEADER_PREFIX + token)
                .post(json(søknad), Kvittering.class);
    }
}
