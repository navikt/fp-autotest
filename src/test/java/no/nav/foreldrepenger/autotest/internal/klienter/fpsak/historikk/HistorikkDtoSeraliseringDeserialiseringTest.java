package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.historikk;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.Hendelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslagDokumentLinkDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagDel;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class HistorikkDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void HendelseTest() {
        test(new Hendelse(new Kode("BT-004","Revurdering")));
        test(new Hendelse(new Kode("BT-004","Revurdering", "revurdering")));
    }

    @Test
    void HistorikkInnslagTest() {
        test(new HistorikkInnslag(123456789,
                HistorikkinnslagType.BREV_BESTILT,
                new Kode("","SBH", "Saksbehandler"),
                new Kode("-", "M", "Mann"),
                List.of(new HistorikkInnslagDokumentLinkDto("1", null, "1234567", "123456", true)),
                List.of(new HistorikkinnslagDel(new Hendelse(new Kode("BT-004","Revurdering"))))));
    }

    @Test
    void HistorikkinnslagDelTest() {
        test(new HistorikkinnslagDel(new Hendelse(new Kode("BT-004","Revurdering"))));
    }




}
