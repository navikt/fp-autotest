package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;
import tools.jackson.core.type.TypeReference;

public class ProsesstaskKlientFelles implements ProsessTaskKlient {

    private static final String PROSESSTASK_URL = "/prosesstask";
    private static final String PROSESSTASK_LIST_URL = PROSESSTASK_URL + "/list";
    private static final String PROSESSTASK_CREATE_URL = PROSESSTASK_URL + "/create";
    private static final List<ProsessTaskStatus> FILTER_KLAR_ELLER_VENTER_SVAR = List.of(ProsessTaskStatus.KLAR, ProsessTaskStatus.VENTER_SVAR); // Default er bare KLAR og VENTER_SVAR
    private static final List<ProsessTaskStatus> ALLE_PROSESSTASK = getStatusFilterDto();

    private final URI baseUrl;
    private final SaksbehandlerRolle saksbehandlerRolle;

    private final String apiName;

    public ProsesstaskKlientFelles(URI baseUrl, SaksbehandlerRolle saksbehandlerRolle, String apiName) {
        this.baseUrl = baseUrl;
        this.apiName = apiName;
        this.saksbehandlerRolle = saksbehandlerRolle;
    }

    private static List<ProsessTaskStatus> getStatusFilterDto() {
        return List.of(ProsessTaskStatus.FEILET, ProsessTaskStatus.VENTER_SVAR, ProsessTaskStatus.SUSPENDERT,
                ProsessTaskStatus.VETO, ProsessTaskStatus.KLAR);
    }

    @Override
    public List<ProsessTaskDataDto> alleProsesstaskPåBehandling() {
        return list(ALLE_PROSESSTASK);
    }

    @Override
    public List<ProsessTaskDataDto> prosesstaskMedKlarEllerVentStatus() {
        return list(FILTER_KLAR_ELLER_VENTER_SVAR);
    }

    @Override
    public List<ProsessTaskDataDto> list(List<ProsessTaskStatus> statusFilter) {
        var requests = statusFilter.stream()
                .map(s -> requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, this.apiName)
                        .uri(fromUri(baseUrl).path(PROSESSTASK_LIST_URL).path(s.name()).build())
                        .POST(HttpRequest.BodyPublishers.ofString("")))
                .toList();
        return requests.stream()
                .map(r -> send(r.build(), new TypeReference<List<ProsessTaskDataDto>>() {}))
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
    }

    @Override
    public void create(ProsessTaskOpprettInputDto prosessTaskOpprettInputDto) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, this.apiName)
                .uri(fromUri(baseUrl)
                        .path(PROSESSTASK_CREATE_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(prosessTaskOpprettInputDto)));
        send(request.build());
    }
}
