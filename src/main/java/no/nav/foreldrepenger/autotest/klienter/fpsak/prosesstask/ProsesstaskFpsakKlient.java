package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask;

import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.FPSAK_BASE;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.SaksbehandlerRolle;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.StatusFilterDto;

public class ProsesstaskFpsakKlient implements ProsessTaskKlient {

    private final ProsesstaskKlientFelles prosesstaskKlient;

    public ProsesstaskFpsakKlient(SaksbehandlerRolle saksbehandlerRolle) {
        prosesstaskKlient = new ProsesstaskKlientFelles(FPSAK_BASE, saksbehandlerRolle);
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
    public List<ProsessTaskDataDto> list(StatusFilterDto statusFilterDto) {
        return prosesstaskKlient.list(statusFilterDto);
    }

    @Override
    public void create(ProsessTaskOpprettInputDto prosessTaskOpprettInputDto) {
        prosesstaskKlient.create(prosessTaskOpprettInputDto);
    }
}
