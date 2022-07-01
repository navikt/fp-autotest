package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.fagsak;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.FagsakStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Sok;
import no.nav.foreldrepenger.common.domain.Saksnummer;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class FagsakDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void FagsakTest() {
        test(new Fagsak(Saksnummer.valueOf("123456789"), FagsakStatus.LØPENDE));
    }

    @Test
    void SokTest() {
        test(new Sok("Søkestreng"));
    }

}
