package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger;

import static jakarta.ws.rs.core.HttpHeaders.LOCATION;
import static jakarta.ws.rs.core.Response.Status.ACCEPTED;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.FPTILBAKE_BASE;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.sendStringRequest;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus.Status.CANCELLED;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus.Status.HALTED;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingIdBasicDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BrukerresponsDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.FeilutbetalingDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.BehandledeAksjonspunkter;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.Saksnummer;

// TODO: Lag en felles klient som brukes av både fpsak og fptilbake?
public class BehandlingerKlient {
    private final Logger LOG = LoggerFactory.getLogger(BehandlingerKlient.class);

    private static final String UUID_NAME = "uuid";
    private static final String SAKSNUMMER_NAME = "saksnummer";

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

    private Behandling følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(HttpRequest.Builder request) {
        var response = sendStringRequest(request.build());
        if (response.statusCode() == ACCEPTED.getStatusCode()) {
            var requestTilStatusEndepunkt = getRequestBuilder()
                    .uri(URI.create(hentRedirectUriFraLocationHeader(response)))
                    .GET();
            return Vent.på(() -> getBehandlingHvisTilgjenglig(requestTilStatusEndepunkt), 30,
                    "Behandling ikke tilgjenglig etter X sekund");
        }
        throw new RuntimeException("Uventet tilstand. Skal ikke være mulig!");
    }

    public void registrerBrukerrespons(BrukerresponsDto brukerresponsDto){
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(REGISTRER_BRUKERRESPONS)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(brukerresponsDto)));
        send(request.build());
    }

    @Step("Henter ut alle behandlinger fra fptilbake på gitt saksnummer")
    public List<Behandling> hentAlleTbkBehandlinger(Saksnummer saksnummer) {
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(BEHANDLINGER_ALLE_URL)
                        .queryParam(SAKSNUMMER_NAME, saksnummer.value())
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<Behandling>>() {}))
                .orElse(List.of());
    }

    @Step("Henter ut en bestemt behandling fra fptilbake")
    public Behandling hentTbkBehandling(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(BEHANDLINGER_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Behandling.class);
    }

    @Step("Henter ut alle aksjonspunkter på en gitt behandling fra fptilbake")
    public List<AksjonspunktDto> hentAlleAksjonspunkter(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(AKSJONSPUNKT_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<AksjonspunktDto>>() {}))
                .orElse(List.of());
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

    @Step("Sender inn aksjonspunkt-data")
    public void postAksjonspunkt(BehandledeAksjonspunkter aksjonspunkter) {
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(AKSJONSPUNKT_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(aksjonspunkter)));
        send(request.build());
    }

    public Behandling hentBehandlingHvisTilgjenglig(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(FPTILBAKE_BASE)
                        .path(BEHANDLINGER_STATUS_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return getBehandlingHvisTilgjenglig(request);
    }

    private Behandling getBehandlingHvisTilgjenglig(HttpRequest.Builder request) {
        var response = sendStringRequest(request.build());
        if (response.statusCode() == 303) {
            return followRedirectOgHentBehandling(URI.create(hentRedirectUriFraLocationHeader(response)));
        }

        var asyncPollingStatus = JacksonBodyHandlers.fromJson(response.body(), AsyncPollingStatus.class);
        if (asyncPollingStatus.getStatus().equals(HALTED) || asyncPollingStatus.getStatus().equals(CANCELLED)) {
            throw new IllegalStateException("Prosesstask i vrang tilstand: " + asyncPollingStatus.getMessage());
        }
        LOG.info("Behandlingen er ikke ferdig prosessert, men har status {}", asyncPollingStatus.getStatus());
        return null;
    }

    private Behandling followRedirectOgHentBehandling(URI redirectUri) {
        var redirect = getRequestBuilder()
                .uri(redirectUri)
                .GET();
        return send(redirect.build(), Behandling.class);
    }

    private static String hentRedirectUriFraLocationHeader(HttpResponse<String> response) {
        return response.headers().firstValue(LOCATION).get();  // TODO: Fiks
    }
}
