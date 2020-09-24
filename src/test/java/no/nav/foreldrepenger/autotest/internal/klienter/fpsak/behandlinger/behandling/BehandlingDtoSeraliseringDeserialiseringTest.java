package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.behandlinger.behandling;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.klienter.fpsak.SerializationTestBase;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class BehandlingDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void BehandlingTest() {
//        test(new Behandling());
    }

}
