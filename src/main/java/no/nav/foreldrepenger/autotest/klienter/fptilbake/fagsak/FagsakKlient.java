package no.nav.foreldrepenger.autotest.klienter.fptilbake.fagsak;

import io.qameta.allure.Step;

import no.nav.foreldrepenger.autotest.klienter.fptilbake.FptilbakeKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

import java.io.IOException;

public class FagsakKlient extends FptilbakeKlient {

    private static String FAGSAK_URL_FORMAT = "/fagsak?saksnummer=%1$s";

    public FagsakKlient(HttpSession session) {
        super(session);
    }

    @Step("Henter fagsak {saksnummer}")
    public Fagsak getFagsak(String saksnummer) throws IOException {
        String url = hentRestRotUrl() + String.format(FAGSAK_URL_FORMAT, saksnummer);
        return getOgHentJson(url, Fagsak.class, StatusRange.STATUS_200);
    }
}
