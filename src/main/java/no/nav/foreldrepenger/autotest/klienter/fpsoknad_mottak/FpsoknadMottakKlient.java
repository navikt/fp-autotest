package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak;

import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.JsonRest;

public class FpsoknadMottakKlient extends JsonRest {

    private final String FPSOKNAD_MOTTAK_PATH="autotest.fpsoknad-mottak.http.routing.api";

    public FpsoknadMottakKlient(HttpSession session) {
        super(session);
    }

    public String hentRestRotUrl() {
        return System.getProperty(FPSOKNAD_MOTTAK_PATH);
    }
}
