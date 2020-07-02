package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.kodeverk;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.BehandlendeEnhet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kodeverk;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class KodeverkDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void BehandlendeEnhetTest() {
        test(new BehandlendeEnhet("123456789", "enhetNavn", "Status"));
    }

    @Test
    void KodeTest() {
        test(new Kode("BT-004","Revurdering"));
        test(new Kode("BT-004","Revurdering", "revurdering"));
    }

    @Test
    void KodeverkTest() {
        // TODO: Er denne testen tilstrekkelig?
        test(new Kodeverk());
    }



}
