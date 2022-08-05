package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.send;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingIdBasicDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprettRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BrukerresponsDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.FeilutbetalingDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter.BehandledeAksjonspunkter;
import no.nav.foreldrepenger.autotest.util.rest.StatusRange;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.vedtak.exception.TekniskException;

public class BehandlingerKlient {

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

    @Step("Oppretter ny tilbakekreving")
    public void putTilbakekreving(BehandlingOpprett behandlingOpprett) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(BEHANDLINGER_OPPRETT)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(behandlingOpprett)));
        send(request.build());
    }

    public void putTilbakekreving(BehandlingOpprettRevurdering behandlingOpprettRevurdering) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(BEHANDLINGER_OPPRETT)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(behandlingOpprettRevurdering)));
        send(request.build());
    }

    public void addVerge(BehandlingIdBasicDto behandlingIdBasicDto) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(LEGG_TIL_VERGE_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(behandlingIdBasicDto)));
        send(request.build());
    }

    public void removeVerge(BehandlingIdBasicDto behandlingIdBasicDto) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(FJERN_VERGE_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(behandlingIdBasicDto)));
        send(request.build());
    }

    public void registrerBrukerrespons(BrukerresponsDto brukerresponsDto){
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(REGISTRER_BRUKERRESPONS)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(brukerresponsDto)));
        send(request.build());
    }

    @Step("Henter ut alle behandlinger fra fptilbake på gitt saksnummer")
    public List<Behandling> hentAlleTbkBehandlinger(Saksnummer saksnummer) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(BEHANDLINGER_ALLE_URL)
                        .queryParam(SAKSNUMMER, saksnummer.value())
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<Behandling>>() {}))
                .orElse(List.of());
    }

    @Step("Henter ut en bestemt behandling fra fptilbake")
    public Behandling hentTbkBehandling(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(BEHANDLINGER_URL)
                        .queryParam(UUID, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Behandling.class);
    }

    @Step("Henter ut alle aksjonspunkter på en gitt behandling fra fptilbake")
    public List<AksjonspunktDto> hentAlleAksjonspunkter(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(AKSJONSPUNKT_URL)
                        .queryParam(UUID, behandlingUuid)
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<AksjonspunktDto>>() {}))
                .orElse(List.of());
    }

    @Description("Henter faktaer som trengs for behandling av Fakta - aksjonspunkt 7003")
    public FeilutbetalingDto hentFeilutbetalingFakta(UUID behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(FEILUTBETALING_FAKTA_URL)
                        .queryParam(UUID, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), FeilutbetalingDto.class);
    }

    @Step("Sender inn aksjonspunkt-data")
    public void postAksjonspunkt(BehandledeAksjonspunkter aksjonspunkter) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(AKSJONSPUNKT_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(aksjonspunkter)));
        send(request.build());
    }

    /*
     * Hent status for behandling
     */
    @Step("Henter status for behandling")
    public AsyncPollingStatus hentStatus(int behandlingUuid) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(BEHANDLINGER_STATUS_URL)
                        .queryParam(UUID, behandlingUuid)
                        .build())
                .GET();

        // TODO: Fiks med redirect eller async polling?
        //  Bruk try catch inn i HttpRequestSender og ikke legg den ut her. Fin ut hvordan vi kan gjøre det.
        try {
            //            var response = send(request.build(), new TypeReference<HttpResponse<String>>() {});
            var response = JavaHttpKlient.getInstance().klient()
                    .send(request.build(), HttpResponse.BodyHandlers.ofString());
            if (StatusRange.STATUS_REDIRECT.inRange(response.statusCode())) {
                return null;
            } else {
                return JacksonBodyHandlers.getObjectmapper().readValue(response.body(), AsyncPollingStatus.class);
            }
        } catch (JsonProcessingException e) {
            throw new TekniskException("F-208314", "Kunne ikke deserialisere objekt til JSON", e);
        } catch (IOException e) {
            throw new TekniskException("F-432937", "IOException ved kommunikasjon med server", e);
        }  catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TekniskException("F-432938", "InterruptedException ved henting av token", e);
        }
    }
}
