package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.fordel;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.klienter.fpsak.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.BehandlendeFagsystem;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.FagsakInformasjon;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostId;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostKnyttning;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.JournalpostMottak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.OpprettSak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.Saksnummer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto.VurderFagsystem;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class FordelDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void BehandlendeFagsystemTest() {
        test(new BehandlendeFagsystem(true, false, true,
                new Saksnummer(123456789L)));
    }

    @Test
    void FagsakInformasjonTest() {
        test(new FagsakInformasjon("123456789", "FagsakInformasjon"));
    }

    @Test
    void JournalpostIdTest() {
        test(new JournalpostId("123456789"));
    }

    @Test
    void JournalpostKnyttingTest() {
        test(new JournalpostKnyttning(new Saksnummer(123456789L), new JournalpostId("213123321")));
        JournalpostKnyttning journalpostKnyttning = new JournalpostKnyttning(new Saksnummer(123456789L), new JournalpostId("213123321"));
        LOG.info(journalpostKnyttning.toString());
    }

    @Test
    void JournalpostMottakTest() {
        test(new JournalpostMottak("123456789", "123456789", LocalDate.now().toString(),
                "behandlingstemaOffisiellKode", "dokumentTypeIdOffisiellKode",
                LocalDate.now().toString(), "payloadXml",123, "dokumentKategoriOffisiellKode"));
    }

    @Test
    void OpprettSakTest() {
        test(new OpprettSak("123456789", "behandlingstemaOffisiellKode", "123456789"));
    }

    @Test
    void SaksnummerTest() {
        test(new Saksnummer(123456789L));
    }

    @Test
    void VurderFagsystemTest() {
        test(new VurderFagsystem("123456789", true, "123456789", "kode",
                List.of(LocalDate.of(2015, 12, 31).toString(), LocalDate.of(2017, 12, 31).toString()),
                LocalDate.of(2015, 12, 31).toString(), LocalDate.of(2015, 12, 31).toString(),
                LocalDate.of(2015, 12, 31).toString(), "Årsak",
                "123456789", "annenpart"));
    }
}
