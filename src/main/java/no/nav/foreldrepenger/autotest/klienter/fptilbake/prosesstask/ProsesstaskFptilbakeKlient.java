package no.nav.foreldrepenger.autotest.klienter.fptilbake.prosesstask;

import static no.nav.foreldrepenger.autotest.klienter.BaseUriProvider.FPTILBAKE_FORVALTNING_BASE;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.ProsessTaskKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.ProsesstaskKlientFelles;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;

public class ProsesstaskFptilbakeKlient implements ProsessTaskKlient {

    private final ProsesstaskKlientFelles prosesstaskKlient;

    public ProsesstaskFptilbakeKlient(SaksbehandlerRolle saksbehandlerRolle) {
        prosesstaskKlient = new ProsesstaskKlientFelles(FPTILBAKE_FORVALTNING_BASE, saksbehandlerRolle, "fptilbake");
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
