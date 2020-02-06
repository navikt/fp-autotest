package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.FptilbakeKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

import java.io.IOException;

public class BehandlingerKlient extends FptilbakeKlient {

    private static final String BEHANDLINGER_URL = "/behandlinger";

    private static final String BEHANDLINGER_OPPRETT = BEHANDLINGER_URL + "/opprett";

    public BehandlingerKlient(HttpSession session) {
        super(session);
    }

    @Step("Oppretter ny tilbakekreving")
    public void putTilbakekreving(BehandlingOpprett behandlingOpprett) throws IOException {
        String url = hentRestRotUrl() + BEHANDLINGER_OPPRETT;
        putJson(url, behandlingOpprett, StatusRange.STATUS_SUCCESS);
    }
}
