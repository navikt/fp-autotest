package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskStatusDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.StatusFilterDto;

public class ProsesstaskKlientFelles implements ProsessTaskKlient {

    private static final String PROSESSTASK_URL = "/prosesstask";
    private static final String PROSESSTASK_LIST_URL = PROSESSTASK_URL + "/list";
    private static final String PROSESSTASK_CREATE_URL = PROSESSTASK_URL + "/create";
    private static final StatusFilterDto FILTER_KLAR_ELLER_VENTER_SVAR = new StatusFilterDto(); // Default er bare KLAR og VENTER_SVAR
    private static final StatusFilterDto ALLE_PROSESSTASK = getStatusFilterDto();

    private final URI baseUrl;

    public ProsesstaskKlientFelles(URI baseUrl) {
        this.baseUrl = baseUrl;
    }

    //    @Pattern(regexp = "FEILET|VENTER_SVAR|SUSPENDERT|VETO|KLAR")
    private static StatusFilterDto getStatusFilterDto() {
        var statusFilterDto = new StatusFilterDto();
        statusFilterDto.setProsessTaskStatuser(List.of(
                new ProsessTaskStatusDto(ProsessTaskStatus.FEILET.name()),
                new ProsessTaskStatusDto(ProsessTaskStatus.VENTER_SVAR.name()),
                new ProsessTaskStatusDto(ProsessTaskStatus.SUSPENDERT.name()),
                new ProsessTaskStatusDto(ProsessTaskStatus.VETO.name()),
                new ProsessTaskStatusDto(ProsessTaskStatus.KLAR.name())
        ));
        return statusFilterDto;
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
    public List<ProsessTaskDataDto> list(StatusFilterDto statusFilterDto) {
        var request = getRequestBuilder()
                .uri(fromUri(baseUrl)
                        .path(PROSESSTASK_LIST_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(statusFilterDto)));
        return Optional.ofNullable(
                send(request.build(), new TypeReference<List<ProsessTaskDataDto>>() {}))
                .orElse(List.of());
    }

    @Override
    public void create(ProsessTaskOpprettInputDto prosessTaskOpprettInputDto) {
        var request = getRequestBuilder()
                .uri(fromUri(baseUrl)
                        .path(PROSESSTASK_CREATE_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(prosessTaskOpprettInputDto)));
        send(request.build());
    }
}
