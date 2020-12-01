package no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering;

import no.nav.foreldrepenger.autotest.klienter.fprisk.FpRiskKlient;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.dto.RisikovurderingRequest;
import no.nav.foreldrepenger.autotest.klienter.fprisk.risikovurdering.dto.RisikovurderingResponse;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

public class RisikovurderingKlient extends FpRiskKlient {

    private static final String RISIKOVURDERING_URL = "/risikovurdering";
    private static final String RISIKOVURDERING_HENT_URL = RISIKOVURDERING_URL + "/hent";

    public RisikovurderingKlient(HttpSession session) {
        super(session);
    }

    public RisikovurderingResponse getRisikovurdering(String uuid) {
        RisikovurderingRequest request = new RisikovurderingRequest(uuid);
        String url = hentRestRotUrl() + RISIKOVURDERING_HENT_URL;
        return postOgHentJson(url, request, RisikovurderingResponse.class, StatusRange.STATUS_SUCCESS);
    }

}
