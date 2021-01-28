package no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask;

import static javax.ws.rs.client.Entity.json;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsessTaskListItemDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsesstaskDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsesstaskResultatDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.SokeFilterDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.FptilbakeJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask.dto.NewProsessTaskDto;

public class ProsesstaskJerseyKlient extends FptilbakeJerseyKlient {

    private static final String PROSESSTASK_URL = "/prosesstask";
    private static final String PROSESSTASK_LIST_URL = PROSESSTASK_URL + "/list";
    private static final String PROSESSTASK_LAUNCH_URL = PROSESSTASK_URL + "/launch";
    private static final String PROSESSTASK_CREATE_URL = PROSESSTASK_URL + "/create";

    public ProsesstaskJerseyKlient() {
        super();
    }

    public List<ProsessTaskListItemDto> list(SokeFilterDto sokeFilter) {
        return client.target(base)
                .path(PROSESSTASK_LIST_URL)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<>() {});
    }

    public ProsesstaskResultatDto launch(ProsesstaskDto prosessTask) {
        return client.target(base)
                .path(PROSESSTASK_LAUNCH_URL)
                .request()
                .post(json(prosessTask), ProsesstaskResultatDto.class);
    }

    public void create(NewProsessTaskDto newProsessTask) {
        client.target(base)
                .path(PROSESSTASK_CREATE_URL)
                .request()
                .post(json(newProsessTask));
    }
}