package no.nav.foreldrepenger.autotest.klienter.fpsak.hendelser;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.hendelser.dto.Hendelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.hendelser.dto.HendelseWrapper;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

public class HendelserKlient extends FpsakKlient {
    private static final String HENDELSER_URL = "/hendelser";

    private static final String HENDELSE_URL = HENDELSER_URL + "/motta";

    public HendelserKlient(HttpSession session) {
        super(session);
    }

    @Step("Sender hendelse")
    public void hendelse(Hendelse hendelse) {
        String url = hentRestRotUrl() + HENDELSE_URL;
        postOgVerifiser(url, new HendelseWrapper(hendelse), StatusRange.STATUS_SUCCESS);
    }

}
