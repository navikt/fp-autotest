package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.fagsak;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.klienter.fpsak.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Sok;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Status;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class FagsakDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void FagsakTest() {
        test(new Fagsak(123456789L, new Kode("BT-004","Revurdering")));
    }

    @Test
    void SokTest() {
        test(new Sok("Søkestreng"));
    }

    @Test
    void StatusTest() {
        test(new Status("Status", "beskjed"));
    }
}
