package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask;

import java.util.List;

import no.nav.vedtak.felles.prosesstask.api.ProsessTaskStatus;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;

public interface ProsessTaskKlient {
    List<ProsessTaskDataDto> alleProsesstaskPåBehandling();

    List<ProsessTaskDataDto> prosesstaskMedKlarEllerVentStatus();

    List<ProsessTaskDataDto> list(List<ProsessTaskStatus> statusFilter);

    void create(ProsessTaskOpprettInputDto prosessTaskOpprettInputDto);
}
