package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger;

import static jakarta.ws.rs.core.HttpHeaders.LOCATION;
import static jakarta.ws.rs.core.Response.Status.ACCEPTED;
import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
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

import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingPaVent;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftedeAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.openam.SaksbehandlerRolle;
import no.nav.foreldrepenger.autotest.util.vent.Vent;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class BehandlingKlientFelles implements BehandlingerKlient {
    private static final Logger LOG = LoggerFactory.getLogger(BehandlingKlientFelles.class);

    public static final String UUID_NAME = "uuid";
    public static final String SAKSNUMMER_NAME = "saksnummer";
    public static final String BEHANDLING_URL = "/behandling";
    public static final String BEHANDLING_AKSJONSPUNKT_URL = BEHANDLING_URL + "/aksjonspunkt";
    public static final String BEHANDLINGER_URL = "/behandlinger";
    private static final String BEHANDLINGER_SETT_PA_VENT_URL = BEHANDLINGER_URL + "/sett-pa-vent";
    private static final String BEHANDLINGER_HENLEGG_URL = BEHANDLINGER_URL + "/henlegg";
    private static final String BEHANDLINGER_GJENOPPTA_URL = BEHANDLINGER_URL + "/gjenoppta";
    private static final String BEHANDLINGER_ALLE_URL = BEHANDLINGER_URL + "/alle";

    private final SaksbehandlerRolle saksbehandlerRolle;
    private final URI baseUrl;
    private final String behandlingStatusPath;
    private final String aksjonspunktPath;

    public BehandlingKlientFelles(SaksbehandlerRolle saksbehandlerRolle, URI baseUrl, String behandlingStatusPath, String aksjonspunktPath) {
        this.saksbehandlerRolle = saksbehandlerRolle;
        this.baseUrl = baseUrl;
        this.behandlingStatusPath = behandlingStatusPath;
        this.aksjonspunktPath = aksjonspunktPath;
    }

    @Override
    public Behandling getBehandling(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                .uri(fromUri(baseUrl)
                        .path(BEHANDLINGER_URL)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return send(request.build(), Behandling.class);
    }

    @Override
    public List<Behandling> alle(Saksnummer saksnummer) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                .uri(fromUri(baseUrl)
                        .path(BEHANDLINGER_ALLE_URL)
                        .queryParam(SAKSNUMMER_NAME, saksnummer.value())
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<Behandling>>() {}))
                .orElse(List.of());
    }

    @Override
    public void settPaVent(BehandlingPaVent behandling) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                .uri(fromUri(baseUrl)
                        .path(BEHANDLINGER_SETT_PA_VENT_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(behandling)));
        send(request.build());
    }

    @Override
    public void henlegg(BehandlingHenlegg behandling) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                .uri(fromUri(baseUrl)
                        .path(BEHANDLINGER_HENLEGG_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(behandling)));
        send(request.build());
    }

    @Override
    public Behandling gjenoppta(BehandlingIdDto behandling) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                .uri(fromUri(baseUrl)
                        .path(BEHANDLINGER_GJENOPPTA_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(behandling)));
        return følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(request);
    }


    // Sjekk om vi kan bruke en async klient her istedenfor.
    @Override
    public Behandling hentBehandlingHvisTilgjenglig(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                .uri(fromUri(baseUrl)
                        .path(behandlingStatusPath)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return getBehandlingHvisTilgjenglig(request);
    }



    /**
     * Aksjonspunkt
     */
    @Override
    public List<Aksjonspunkt> hentAlleAksjonspunkter(UUID behandlingUuid) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                .uri(fromUri(baseUrl)
                        .path(aksjonspunktPath)
                        .queryParam(UUID_NAME, behandlingUuid)
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<Aksjonspunkt>>() {}))
                .orElse(List.of());
    }

    @Override
    public void postBehandlingAksjonspunkt(BekreftedeAksjonspunkter aksjonspunkter) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                .uri(fromUri(baseUrl)
                        .path(BEHANDLING_AKSJONSPUNKT_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(aksjonspunkter)));
        send(request.build());
    }


    public Behandling følgRedirectTilStatusOgReturnerBehandlingNårTilgjenglig(HttpRequest.Builder request) {
        var response = sendStringRequest(request.build());
        if (response.statusCode() == ACCEPTED.getStatusCode()) {
            var requestTilStatusEndepunkt = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                    .uri(URI.create(hentRedirectUriFraLocationHeader(response)))
                    .GET();
            return Vent.på(() -> getBehandlingHvisTilgjenglig(requestTilStatusEndepunkt), 30,
                    "Behandling ikke tilgjenglig etter X sekund");
        }
        throw new RuntimeException("Uventet tilstand. Skal ikke være mulig!");
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
        LOG.debug("Behandlingen er ikke ferdig prosessert, men har status {}", asyncPollingStatus.getStatus());
        return null;
    }

    private Behandling followRedirectOgHentBehandling(URI redirectUri) {
        var redirect = requestMedInnloggetSaksbehandler(saksbehandlerRolle)
                .uri(redirectUri)
                .GET();
        return send(redirect.build(), Behandling.class);
    }

    private static String hentRedirectUriFraLocationHeader(HttpResponse<String> response) {
        var locationHeader = response.headers().firstValue(LOCATION);
        if (locationHeader.isPresent()) {
            return locationHeader.get();
        }
        throw new IllegalStateException("Location header er ikke returnert av status endepuknkt! Noe er galt!");
    }

}
