package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.behandlinger.behandling.uttak;

import static java.time.LocalDate.now;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.UDEFINERT;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak.ARBEID;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak;
import no.nav.foreldrepenger.autotest.internal.klienter.fpsak.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Arbeidsgiver;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.BehandlingMedUttaksperioderDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Stonadskontoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakDokumentasjon;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriodeAktivitet;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPerioder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class UttakSerializationDeserialiseringTest extends SerializationTestBase {

    private Arbeidsgiver arbeidsgiver = null;
    private UttakResultatPeriode uttakResultatPeriode = null;

    @Test
    void ArbeidsgiverTest() {
        test(lagArbeidsgiver());
    }

    @Test
    void BehandlingMedUttaksperioderDtoTest() {
        test(new BehandlingMedUttaksperioderDto(
                new BehandlingIdDto(123456789L,123456789L, UUID.randomUUID()),
                List.of(lagUttakResultatPeriode())));
    }


    @Test
    void SaldoerTest() {
        test(new Saldoer(now(), Map.of(FELLESPERIODE, new Stonadskontoer(FELLESPERIODE, 100, 42))));
    }

    @Test
    void StønadskontoTest() {
        test(FELLESPERIODE);
        test(UDEFINERT); // TODO Fiks denne
    }

    @Test
    void StonadskontoerTest() {
        test(new Stonadskontoer(FELLESPERIODE, 100, 42));
    }


    @Test
    void UttakDokumentasjonTest() {
        test(new UttakDokumentasjon(LocalDate.now(), LocalDate.now().plusMonths(1)));
    }

    @Test
    void UttakResultatPeriodeTest() {
        test(lagUttakResultatPeriode());
    }

    @Test
    void UttakResultatPeriodeAktivitetTest() {
        test(lagUttakResultatPeriodeAktivitet());
    }

    @Test
    void UttakResultatPerioderTest() {
        test(new UttakResultatPerioder(List.of(lagUttakResultatPeriode()),
                List.of(lagUttakResultatPeriode(), lagUttakResultatPeriode())));
    }

    private Arbeidsgiver lagArbeidsgiver() {
        if (arbeidsgiver == null) {
            arbeidsgiver = new Arbeidsgiver("Indikator", "NAV", "1234567", true);
        }
        return arbeidsgiver;
    }

    private UttakResultatPeriode lagUttakResultatPeriode() {
        if (uttakResultatPeriode == null) {
            uttakResultatPeriode = new UttakResultatPeriode(LocalDate.now().minusDays(1), LocalDate.now(),
                    List.of(lagUttakResultatPeriodeAktivitet()), new Kode("2001"), "Begrunnelses",
                    new Kode("2001"), new Kode("2432"), new Kode("1234"),false, true,
                    BigDecimal.valueOf(87), true, new Kode("5432"),ARBEID,
                    OppholdÅrsak.FEDREKVOTE_ANNEN_FORELDER, lagUttakResultatPeriodeAktivitet());
        }
        return uttakResultatPeriode;
    }

    private UttakResultatPeriodeAktivitet lagUttakResultatPeriodeAktivitet() {
        return new UttakResultatPeriodeAktivitet(FORELDREPENGER_FØR_FØDSEL, BigDecimal.valueOf(10),
                BigDecimal.valueOf(50), BigDecimal.valueOf(50), new Kode("5003"),
                lagArbeidsgiver(), BigDecimal.valueOf(120), "UAFA-123-21312", true);
    }


}
