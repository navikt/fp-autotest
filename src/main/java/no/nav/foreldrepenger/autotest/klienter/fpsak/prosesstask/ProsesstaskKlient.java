package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskStatusDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.StatusFilterDto;

public class ProsesstaskKlient {

    private static final String PROSESSTASK_URL = "/prosesstask";
    private static final String PROSESSTASK_LIST_URL = PROSESSTASK_URL + "/list";
    private static final StatusFilterDto FILTER_KLAR_ELLER_VENTER_SVAR = new StatusFilterDto(); // Default er bare KLAR og VENTER_SVAR
    private static final StatusFilterDto ALLE_PROSESSTASK = getStatusFilterDto();

    private static StatusFilterDto getStatusFilterDto() {
        var statusFilterDto = new StatusFilterDto();
        statusFilterDto.setProsessTaskStatuser(List.of(
                new ProsessTaskStatusDto("FEILET"),
                new ProsessTaskStatusDto("VENTER_SVAR,"),
                new ProsessTaskStatusDto("SUSPENDERT"),
                new ProsessTaskStatusDto("VETO"),
                new ProsessTaskStatusDto("KLAR")
                ));
        return statusFilterDto;
    }

    public List<ProsessTaskDataDto> alleProsesstaskPåBehandling() {
        return list(ALLE_PROSESSTASK);
    }

    public List<ProsessTaskDataDto> prosesstaskMedKlarEllerVentStatus() {
        return list(FILTER_KLAR_ELLER_VENTER_SVAR);
    }

    public List<ProsessTaskDataDto> list(StatusFilterDto statusFilterDto) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSAK_BASE)
                        .path(PROSESSTASK_LIST_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(statusFilterDto)));
        return Optional.ofNullable(
                send(request.build(), new TypeReference<List<ProsessTaskDataDto>>() {}))
                .orElse(List.of());
    }
}
