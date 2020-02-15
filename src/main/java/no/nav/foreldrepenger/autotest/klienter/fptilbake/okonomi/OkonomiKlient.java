package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.FptilbakeKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

import java.io.IOException;

public class OkonomiKlient extends FptilbakeKlient {

    private static final String GRUNNLAG_URL = "/grunnlag?behandlingId=";

    public OkonomiKlient(HttpSession session) {
        super(session);
    }

    @Step
    public void putGrunnlag(Kravgrunnlag kravgrunnlag, int behandlingId) throws IOException {
        String url = hentRestRotUrl() + GRUNNLAG_URL + behandlingId;
        postOgVerifiser(url, kravgrunnlag, StatusRange.STATUS_SUCCESS);
    }
}
