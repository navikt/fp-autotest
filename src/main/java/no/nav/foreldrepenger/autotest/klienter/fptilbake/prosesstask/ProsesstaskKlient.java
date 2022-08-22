package no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask;

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
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.StatusFilterDto;

public class ProsesstaskKlient {

    private static final String PROSESSTASK_URL = "/prosesstask";
    private static final String PROSESSTASK_LIST_URL = PROSESSTASK_URL + "/list";
    private static final String PROSESSTASK_CREATE_URL = PROSESSTASK_URL + "/create";
    private static final StatusFilterDto FILTER_KLAR_ELLER_VENTER_SVAR = new StatusFilterDto();

    public List<ProsessTaskDataDto> prosesstaskMedKlarEllerVentStatus() {
        return list(FILTER_KLAR_ELLER_VENTER_SVAR);
    }

    public List<ProsessTaskDataDto> list(StatusFilterDto statusFilterDto) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(PROSESSTASK_LIST_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(statusFilterDto)));
        return Optional.ofNullable(send(request.build(), new TypeReference<List<ProsessTaskDataDto>>() {}))
                .orElse(List.of());
    }

    public void create(ProsessTaskOpprettInputDto prosessTaskOpprettInputDto) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(PROSESSTASK_CREATE_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(prosessTaskOpprettInputDto)));
        send(request.build());
    }
}
