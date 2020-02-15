package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.FptilbakeKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BehandlingerKlient extends FptilbakeKlient {

    private static final String BEHANDLINGER_URL = "/behandlinger";
    private static final String BEHANDLINGER_GET_URL = "/behandlinger?behandlingId=%s";

    private static final String BEHANDLINGER_OPPRETT = BEHANDLINGER_URL + "/opprett";
    private static final String BEHANDLINGER_ALLE_URL = BEHANDLINGER_URL + "/alle?saksnummer=%s";

    private static final String AKSJONSPUNKT_URL = "/behandling/aksjonspunkt";
    private static final String AKSJONSPUNKT_GET_URL = AKSJONSPUNKT_URL + "?behandlingId=%s";

    public BehandlingerKlient(HttpSession session) {
        super(session);
    }

    @Step("Oppretter ny tilbakekreving")
    public void putTilbakekreving(BehandlingOpprett behandlingOpprett) throws IOException {
        String url = hentRestRotUrl() + BEHANDLINGER_OPPRETT;
        postOgVerifiser(url, behandlingOpprett, StatusRange.STATUS_SUCCESS);
    }

    public List<Behandling> hentAlleTbkBehandlinger(long saksnummer) throws IOException {
        String url = hentRestRotUrl() + String.format(BEHANDLINGER_ALLE_URL, saksnummer);
        return getOgHentJson(url, hentObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, Behandling.class), StatusRange.STATUS_SUCCESS);
    }

    public Behandling hentTbkBehandling(int behandlingId) throws IOException {
        String url = hentRestRotUrl() + String.format(BEHANDLINGER_GET_URL, behandlingId);
        return getOgHentJson(url, Behandling.class, StatusRange.STATUS_SUCCESS);
    }

    public List<AksjonspunktDto> hentAlleAksjonspunkter(int behandlingId) throws IOException {
        String url = hentRestRotUrl() + String.format(AKSJONSPUNKT_GET_URL, behandlingId);
        return getOgHentJson(url, hentObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, AksjonspunktDto.class), StatusRange.STATUS_SUCCESS);
    }

}
