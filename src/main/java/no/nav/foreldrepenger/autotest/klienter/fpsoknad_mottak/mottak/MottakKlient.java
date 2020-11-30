package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak;

import java.util.HashMap;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.FpsoknadMottakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.mottak.dto.Kvittering;
import no.nav.foreldrepenger.autotest.søknad.modell.Søknad;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

public class MottakKlient extends FpsoknadMottakKlient {

    private static final String MOTTAK_PATH="/mottak";
    private static final String MOTTAK_SEND_PATH = MOTTAK_PATH + "/send";

    public MottakKlient(HttpSession session) {
        super(session);
    }

    public Kvittering sendSøknad(String token, Søknad søknad) {
        var url = hentRestRotUrl() + MOTTAK_SEND_PATH;
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        return postOgHentJson(url, søknad, headers, Kvittering.class, StatusRange.STATUS_SUCCESS);
    }
}
