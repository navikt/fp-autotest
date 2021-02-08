package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger;

import static javax.ws.rs.client.Entity.json;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.FptilbakeJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingIdBasicDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprettRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BrukerresponsDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.FeilutbetalingDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.BehandledeAksjonspunkter;
import no.nav.foreldrepenger.autotest.util.rest.StatusRange;

public class BehandlingerJerseyKlient extends FptilbakeJerseyKlient {

    private static final String UUID = "uuid";
    private static final String SAKSNUMMER = "saksnummer";

    private static final String BEHANDLINGER_URL = "/behandlinger";
    private static final String BEHANDLINGER_STATUS_URL = "/behandlinger/status";

    private static final String BEHANDLINGER_OPPRETT = BEHANDLINGER_URL + "/opprett";
    private static final String BEHANDLINGER_ALLE_URL = BEHANDLINGER_URL + "/alle";

    private static final String VERGE_URL = "/verge";
    private static final String LEGG_TIL_VERGE_URL = VERGE_URL + "/opprett";
    private static final String FJERN_VERGE_URL = VERGE_URL + "/fjern";

    private static final String REGISTRER_BRUKERRESPONS = "/varsel/respons/registrer";

    private static final String AKSJONSPUNKT_URL = "/behandling/aksjonspunkt";

    private static final String FEILUTBETALING_FAKTA_URL = "/behandlingfakta/hent-fakta/feilutbetaling";

    public BehandlingerJerseyKlient() {
        super();
    }

    @Step("Oppretter ny tilbakekreving")
    public void putTilbakekreving(BehandlingOpprett behandlingOpprett) {
        client.target(base)
                .path(BEHANDLINGER_OPPRETT)
                .request()
                .post(json(behandlingOpprett));
    }

    public void putTilbakekreving(BehandlingOpprettRevurdering behandlingOpprettRevurdering) {
        client.target(base)
                .path(BEHANDLINGER_OPPRETT)
                .request()
                .post(json(behandlingOpprettRevurdering));
    }

    public void addVerge(BehandlingIdBasicDto behandlingIdBasicDto) {
        client.target(base)
                .path(LEGG_TIL_VERGE_URL)
                .request()
                .post(json(behandlingIdBasicDto));
    }

    public void removeVerge(BehandlingIdBasicDto behandlingIdBasicDto) {
        client.target(base)
                .path(FJERN_VERGE_URL)
                .request()
                .post(json(behandlingIdBasicDto));
    }

    public void registrerBrukerrespons(BrukerresponsDto brukerresponsDto){
        client.target(base)
                .path(REGISTRER_BRUKERRESPONS)
                .request()
                .post(json(brukerresponsDto));
    }

    @Step("Henter ut alle behandlinger fra fptilbake på gitt saksnummer")
    public List<Behandling> hentAlleTbkBehandlinger(long saksnummer) {
        return client.target(base)
                .path(BEHANDLINGER_ALLE_URL)
                .queryParam(SAKSNUMMER, saksnummer)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<>() {});
    }

    @Step("Henter ut en bestemt behandling fra fptilbake")
    public Behandling hentTbkBehandling(UUID behandlingUuid) {
        return client.target(base)
                .path(BEHANDLINGER_URL)
                .queryParam(UUID, behandlingUuid)
                .request()
                .get(Behandling.class);
    }

    @Step("Henter ut alle aksjonspunkter på en gitt behandling fra fptilbake")
    public List<AksjonspunktDto> hentAlleAksjonspunkter(UUID behandlingUuid) {
        return client.target(base)
                .path(AKSJONSPUNKT_URL)
                .queryParam(UUID, behandlingUuid)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<>() {});
    }

    @Description("Henter faktaer som trengs for behandling av Fakta - aksjonspunkt 7003")
    public FeilutbetalingDto hentFeilutbetalingFakta(UUID behandlingUuid) {
        return client.target(base)
                .path(FEILUTBETALING_FAKTA_URL)
                .queryParam(UUID, behandlingUuid)
                .request()
                .get(FeilutbetalingDto.class);
    }

    @Step("Sender inn aksjonspunkt-data")
    public void postAksjonspunkt(BehandledeAksjonspunkter aksjonspunkter) {
        client.target(base)
                .path(AKSJONSPUNKT_URL)
                .request()
                .post(json(aksjonspunkter));
    }

    /*
     * Hent status for behandling
     */
    @Step("Henter status for behandling")
    public AsyncPollingStatus hentStatus(int behandlingUuid) {
        var response = client.target(base)
                .path(BEHANDLINGER_STATUS_URL)
                .queryParam(UUID, behandlingUuid)
                .request(APPLICATION_JSON_TYPE)
                .get(Response.class);
        if (StatusRange.STATUS_REDIRECT.inRange(response.getStatus())) {
            return null;
        } else {
            return response.readEntity(AsyncPollingStatus.class);
        }
    }
}
