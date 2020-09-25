package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.prosesstask;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.klienter.fpsak.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsessTaskListItemDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsessTaskStatusDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsesstaskDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.ProsesstaskResultatDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.SokeFilterDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto.TaskParametereDto;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class ProsesstaskDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void ProsesstaskDtoTest() {
        test(new ProsesstaskDto(123456789, "AVSLÅTT"));
    }

    @Test
    void ProsessTaskListItemDtoTest() {
        test(new ProsessTaskListItemDto(123,"taskType", "nesteKjøringEtter","gruppe",
                "sekvens", "status", "sistKjørt", "sistFeilKode",
                new TaskParametereDto("CALLID-123456789", "123456789", "123456789", "123456789","batch.runner")));
    }

    @Test
    void ProsesstaskResultatDtoTest() {
        test(new ProsesstaskResultatDto(123,"prosessTaskStatus", "nesteKjoeretidspunkt"));
    }

    @Test
    void ProsessTaskStatusDtoTest() {
        test(new ProsessTaskStatusDto("prosessTaskStatusName"));
    }

    @Test
    void SokeFilterDtoTest() {
        test(new SokeFilterDto(List.of(new ProsessTaskStatusDto("prosessTaskStatusName1"),
                new ProsessTaskStatusDto("prosessTaskStatusName2")), LocalDateTime.now(),
                LocalDateTime.now()));
    }

    @Test
    void TaskParametereDtoTest() {
        test(new TaskParametereDto("CALLID-123456789", "123456789", "123456789", "123456789","batch.runner"));
    }


}
