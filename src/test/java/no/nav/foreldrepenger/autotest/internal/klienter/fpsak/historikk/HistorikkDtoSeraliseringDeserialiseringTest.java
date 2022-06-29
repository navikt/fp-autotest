package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.historikk;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.Hendelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslagDokumentLinkDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class HistorikkDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void HendelseTest() {
        test(new Hendelse("BREV_SENT"));
        test(new Hendelse("BEH_STARTET"));
    }

    @Test
    void HistorikkInnslagTst() {
        test(new HistorikkInnslag(UUID.randomUUID(), HistorikkinnslagType.BREV_SENT, "SØKER", "M",
                List.of(new HistorikkInnslagDokumentLinkDto("Test", URI.create("http://fpsak/fpsak"), "1234567", "122345", false))));
    }

}
