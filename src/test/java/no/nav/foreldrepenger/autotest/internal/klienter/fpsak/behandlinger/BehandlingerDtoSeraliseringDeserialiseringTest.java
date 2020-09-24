package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.behandlinger;

import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus.Status.CANCELLED;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.klienter.fpsak.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.AsyncPollingStatus;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingByttEnhet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingHenlegg;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdPost;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingNy;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingPaVent;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.KlageVurderingResultatAksjonspunktMellomlagringDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class BehandlingerDtoSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void AsyncPollingStatusTest() {
        test(new AsyncPollingStatus(CANCELLED, "eta", "message", 1500, "location",
                "httpURI", false, true));
    }

    @Test
    void BehandlingByttEnhetTest() {
        test(new BehandlingByttEnhet(12345678, 442,"enhetsnavn","E-123",
                "begrunnelse"));
    }

    @Test
    void BehandlingHenleggTest() {
        test(new BehandlingHenlegg(12345678, 442,"enhetsnavn","begrunnelse"));
    }

    @Test
    void BehandlingIdDtoTest() {
        test(new BehandlingIdDto(123456789L,123456789L, UUID.randomUUID()));
    }

    @Test
    void BehandlingIdPostTest() {
        test(new BehandlingIdPost(123456789, 42));
    }

    @Test
    void BehandlingNyTest() {
        test(new BehandlingNy(123456789L, "BT-006", "2001", false));
    }

    @Test
    void BehandlingPaVentTest() {
        test(new BehandlingPaVent(12345678, 442, LocalDate.now(),
                new Kode("2003")));
    }

    @Test
    void KlageVurderingResultatAksjonspunktMellomlagringDtoTest() {
        test(new KlageVurderingResultatAksjonspunktMellomlagringDto("2001", "begrunnelse",
                123456789, "fritekst", "Klagemedholdårsak",
                "Vurdering","VurderingOmgjøring"));
    }
}
