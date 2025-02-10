package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask;

import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;

import java.util.List;

import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.FPSAK_BASE;

public class ProsesstaskFpsakKlient implements ProsessTaskKlient {

    private final ProsesstaskKlientFelles prosesstaskKlient;

    public ProsesstaskFpsakKlient() {
        prosesstaskKlient = new ProsesstaskKlientFelles(FPSAK_BASE, "fpsak");
    }

    public ProsesstaskFpsakKlient(SaksbehandlerRolle saksbehandlerRolle) {
        prosesstaskKlient = new ProsesstaskKlientFelles(FPSAK_BASE, saksbehandlerRolle, "fpsak");
    }

    @Override
    public List<ProsessTaskDataDto> alleProsesstaskPåBehandling() {
        return prosesstaskKlient.alleProsesstaskPåBehandling();
    }

    @Override
    public List<ProsessTaskDataDto> prosesstaskMedKlarEllerVentStatus() {
        return prosesstaskKlient.prosesstaskMedKlarEllerVentStatus();
    }

    @Override
    public List<ProsessTaskDataDto> list(List<ProsessTaskStatus> statusFilterDto) {
        return prosesstaskKlient.list(statusFilterDto);
    }

    @Override
    public void create(ProsessTaskOpprettInputDto prosessTaskOpprettInputDto) {
        prosesstaskKlient.create(prosessTaskOpprettInputDto);
    }
}
