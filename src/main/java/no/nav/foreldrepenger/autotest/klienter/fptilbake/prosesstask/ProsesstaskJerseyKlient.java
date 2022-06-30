package no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask;

import static jakarta.ws.rs.client.Entity.json;

import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.FptilbakeJerseyKlient;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.StatusFilterDto;

public class ProsesstaskJerseyKlient extends FptilbakeJerseyKlient {

    private static final String PROSESSTASK_URL = "/prosesstask";
    private static final String PROSESSTASK_LIST_URL = PROSESSTASK_URL + "/list";
    private static final String PROSESSTASK_CREATE_URL = PROSESSTASK_URL + "/create";
    private static final StatusFilterDto FILTER_KLAR_ELLER_VENTER_SVAR = new StatusFilterDto();

    public ProsesstaskJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }

    public List<ProsessTaskDataDto> prosesstaskMedKlarEllerVentStatus() {
        return list(FILTER_KLAR_ELLER_VENTER_SVAR);
    }

    public List<ProsessTaskDataDto> list(StatusFilterDto statusFilterDto) {
        return Optional.ofNullable(client.target(base)
                        .path(PROSESSTASK_LIST_URL)
                        .request()
                        .post(json(statusFilterDto), Response.class)
                        .readEntity(new GenericType<List<ProsessTaskDataDto>>() {}))
                .orElse(List.of());
    }

    public void create(ProsessTaskOpprettInputDto prosessTaskOpprettInputDto) {
        client.target(base)
                .path(PROSESSTASK_CREATE_URL)
                .request()
                .post(json(prosessTaskOpprettInputDto));
    }
}
