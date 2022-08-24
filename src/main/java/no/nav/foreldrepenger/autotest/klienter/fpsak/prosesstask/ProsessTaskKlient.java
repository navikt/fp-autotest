package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask;

import java.util.List;

import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskDataDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.ProsessTaskOpprettInputDto;
import no.nav.vedtak.felles.prosesstask.rest.dto.StatusFilterDto;

public interface ProsessTaskKlient {
    List<ProsessTaskDataDto> alleProsesstaskPåBehandling();

    List<ProsessTaskDataDto> prosesstaskMedKlarEllerVentStatus();

    List<ProsessTaskDataDto> list(StatusFilterDto statusFilterDto);

    void create(ProsessTaskOpprettInputDto prosessTaskOpprettInputDto);
}
