package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger;

import java.util.ArrayList;
import java.util.List;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.FptilbakeKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprettRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.FeilutbetalingDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.BehandledeAksjonspunkter;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

public class BehandlingerKlient extends FptilbakeKlient {

    private static final String BEHANDLINGER_URL = "/behandlinger";
    private static final String BEHANDLINGER_GET_URL = "/behandlinger?behandlingId=%s";

    private static final String BEHANDLINGER_OPPRETT = BEHANDLINGER_URL + "/opprett";
    private static final String BEHANDLINGER_ALLE_URL = BEHANDLINGER_URL + "/alle?saksnummer=%s";

    private static final String AKSJONSPUNKT_URL = "/behandling/aksjonspunkt";
    private static final String AKSJONSPUNKT_GET_URL = AKSJONSPUNKT_URL + "?behandlingId=%s";

    private static final String FEILUTBETALING_FAKTA_URL = "/behandlingfakta/hent-fakta/feilutbetaling?behandlingId=%s";

    public BehandlingerKlient(HttpSession session) {
        super(session);
    }

    @Step("Oppretter ny tilbakekreving")
    public void putTilbakekreving(BehandlingOpprett behandlingOpprett) {
        String url = hentRestRotUrl() + BEHANDLINGER_OPPRETT;
        postOgVerifiser(url, behandlingOpprett, StatusRange.STATUS_SUCCESS);
    }
    public void putTilbakekreving(BehandlingOpprettRevurdering behandlingOpprettRevurdering) {
        String url = hentRestRotUrl() + BEHANDLINGER_OPPRETT;
        postOgVerifiser(url, behandlingOpprettRevurdering, StatusRange.STATUS_SUCCESS);
    }

    @Step("Henter ut alle behandlinger fra fptilbake på gitt saksnummer")
    public List<Behandling> hentAlleTbkBehandlinger(long saksnummer) {
        String url = hentRestRotUrl() + String.format(BEHANDLINGER_ALLE_URL, saksnummer);
        return getOgHentJson(url, hentObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, Behandling.class), StatusRange.STATUS_SUCCESS);
    }

    @Step("Henter ut en bestemt behandling fra fptilbake")
    public Behandling hentTbkBehandling(int behandlingId) {
        String url = hentRestRotUrl() + String.format(BEHANDLINGER_GET_URL, behandlingId);
        return getOgHentJson(url, Behandling.class, StatusRange.STATUS_SUCCESS);
    }

    @Step("Henter ut alle aksjonspunkter på en gitt behandling fra fptilbake")
    public List<AksjonspunktDto> hentAlleAksjonspunkter(int behandlingId) {
        String url = hentRestRotUrl() + String.format(AKSJONSPUNKT_GET_URL, behandlingId);
        return getOgHentJson(url, hentObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, AksjonspunktDto.class), StatusRange.STATUS_SUCCESS);
    }

    @Description("Henter faktaer som trengs for behandling av Fakta - aksjonspunkt 7003")
    public FeilutbetalingDto hentFeilutbetalingFakta(int behandlingId) {
        String url = hentRestRotUrl() + String.format(FEILUTBETALING_FAKTA_URL, behandlingId);
        return getOgHentJson(url, FeilutbetalingDto.class, StatusRange.STATUS_SUCCESS);
    }

    @Step("Sender inn aksjonspunkt-data")
    public void postAksjonspunkt(BehandledeAksjonspunkter aksjonspunkter) {
        String url = hentRestRotUrl() + AKSJONSPUNKT_URL;
        postOgVerifiser(url, aksjonspunkter, StatusRange.STATUS_SUCCESS);
    }

}
