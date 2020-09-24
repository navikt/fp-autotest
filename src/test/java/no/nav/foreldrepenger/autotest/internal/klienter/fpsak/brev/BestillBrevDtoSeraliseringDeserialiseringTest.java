package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.brev;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.klienter.fpsak.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.brev.dto.BestillBrev;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class BestillBrevDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void BestillBrevTest() {
        test(new BestillBrev(123456789, "mottaker", "Kode-12F","Fritekst her",
                "2001", "2005"));
    }

}
