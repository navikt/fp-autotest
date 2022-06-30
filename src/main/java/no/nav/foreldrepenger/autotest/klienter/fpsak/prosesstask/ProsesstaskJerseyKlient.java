package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask;

import static jakarta.ws.rs.client.Entity.json;
import static no.nav.foreldrepenger.common.mapper.DefaultJsonMapper.MAPPER;

import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.StatusFilterDto;

public class ProsesstaskJerseyKlient extends FpsakJerseyKlient {

    private static final String PROSESSTASK_URL = "/prosesstask";
    private static final String PROSESSTASK_LIST_URL = PROSESSTASK_URL + "/list";
    private static final StatusFilterDto FILTER_KLAR_ELLER_VENTER_SVAR = new StatusFilterDto();

    public ProsesstaskJerseyKlient(ClientRequestFilter filter) {
        super(MAPPER, filter);
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
}
