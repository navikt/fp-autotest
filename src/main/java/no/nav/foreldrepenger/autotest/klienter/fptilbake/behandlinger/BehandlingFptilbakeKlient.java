package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.FPTILBAKE_BASE;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingKlientFelles.BEHANDLINGER_URL;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingKlientFelles.BEHANDLING_URL;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingKlientFelles.UUID_NAME;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingKlientFelles.følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.UUID;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingKlientFelles;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.BehandlingerKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingPaVent;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftedeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingIdBasicDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BrukerresponsDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.FeilutbetalingDto;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class BehandlingFptilbakeKlient implements BehandlingerKlient {

    private final BehandlingKlientFelles behandlingKlientFelles = new BehandlingKlientFelles(FPTILBAKE_BASE, BEHANDLINGER_STATUS_FPTILBAKE_URL, AKSJONSPUNKT_FPTILBAKE_PATH);

    private static final String BEHANDLINGER_OPPRETT = BEHANDLINGER_URL + "/opprett";
    private static final String BEHANDLINGER_STATUS_FPTILBAKE_URL = BEHANDLINGER_URL + "/status";

    private static final String VERGE_URL = "/verge";
    private static final String LEGG_TIL_VERGE_URL = VERGE_URL + "/opprett";
    private static final String FJERN_VERGE_URL = VERGE_URL + "/fjern";

    private static final String REGISTRER_BRUKERRESPONS = "/varsel/respons/registrer";

    private static final String AKSJONSPUNKT_FPTILBAKE_PATH = BEHANDLING_URL + "/aksjonspunkt";

    private static final String FEILUTBETALING_FAKTA_URL = "/behandlingfakta/hent-fakta/feilutbetaling";


    @Override
    public Behandling getBehandling(UUID behandlingUuid) {
        return behandlingKlientFelles.getBehandling(behandlingUuid);
    }

    @Override
    public Behandling hentBehandlingHvisTilgjenglig(UUID behandlingUuid) {
        return behandlingKlientFelles.hentBehandlingHvisTilgjenglig(behandlingUuid);
    }

    @Override
    public List<Behandling> alle(Saksnummer saksnummer) {
        return behandlingKlientFelles.alle(saksnummer);
    }

    @Override
    public void settPaVent(BehandlingPaVent behandling) {
        behandlingKlientFelles.settPaVent(behandling);
    }

    @Override
    public void henlegg(BehandlingHenlegg behandling) {
        behandlingKlientFelles.henlegg(behandling);
    }

    @Override
    public Behandling gjenoppta(BehandlingIdDto behandling) {
        return behandlingKlientFelles.gjenoppta(behandling);
    }

    @Override
    public void postBehandlingAksjonspunkt(BekreftedeAksjonspunkter aksjonspunkter) {
        behandlingKlientFelles.postBehandlingAksjonspunkt(aksjonspunkter);
    }

    @Step("Oppretter ny tilbakekreving")
    public Behandling opprettTilbakekrevingManuelt(BehandlingOpprett behandlingOpprett) {
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(BEHANDLINGER_OPPRETT)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(behandlingOpprett)));

        return følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(request);
    }

    public Behandling addVerge(BehandlingIdBasicDto behandlingIdBasicDto) {
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(LEGG_TIL_VERGE_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(behandlingIdBasicDto)));
        return følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(request);
    }

    public Behandling removeVerge(BehandlingIdBasicDto behandlingIdBasicDto) {
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(FJERN_VERGE_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(behandlingIdBasicDto)));
        return følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(request);
    }

    public void registrerBrukerrespons(BrukerresponsDto brukerresponsDto){
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(REGISTRER_BRUKERRESPONS)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(brukerresponsDto)));
        send(request.build());
    }

    @Description("Henter faktaer som trengs for behandling av Fakta - aksjonspunkt 7003")
    public FeilutbetalingDto hentFeilutbetalingFakta(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(FEILUTBETALING_FAKTA_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), FeilutbetalingDto.class);
    }

    @Override
    @Step("Henter ut alle aksjonspunkter på en gitt behandling fra fptilbake")
    public List<Aksjonspunkt> hentAlleAksjonspunkter(UUID behandlingUuid) {
        return behandlingKlientFelles.hentAlleAksjonspunkter(behandlingUuid);
    }
}
