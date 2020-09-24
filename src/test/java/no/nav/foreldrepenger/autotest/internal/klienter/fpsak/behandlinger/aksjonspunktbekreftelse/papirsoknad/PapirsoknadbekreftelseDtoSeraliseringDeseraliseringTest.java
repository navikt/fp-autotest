package no.nav.foreldrepenger.autotest.internal.klienter.fpsak.behandlinger.aksjonspunktbekreftelse.papirsoknad;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak.ARBEID;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.internal.klienter.fpsak.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadEndringForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad.PapirSoknadEngangstonadBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.GraderingPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.PermisjonPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.UtsettelsePeriodeDto;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class PapirsoknadbekreftelseDtoSeraliseringDeseraliseringTest extends SerializationTestBase {

    private PermisjonPeriodeDto permisjonPeriodeDto = null;
    private GraderingPeriodeDto graderingPeriodeDto = null;
    private UtsettelsePeriodeDto utsettelsePeriodeDto = null;

    @Test
    void AvklarAktiviteterBekreftelseTest() {
        test(new PapirSoknadEndringForeldrepengerBekreftelse("FODSL","FP", "MOR", LocalDate.now(),
                lagFordelingDto()));
    }

    @Test
    void PermisjonPeriodeDtoTest() {
        test(lagPermisjonPeriodeDto());
    }

    @Test
    void GraderingPeriodeDtoTest() {
        test(lagGraderingPeriodeDto());
    }

    @Test
    void UtsettelsePeriodeDtoTest() {
        test(lagUtsettelsePeriodeDto());
    }

    @Test
    void PapirSoknadEngangstonadBekreftelseTest() {
        test(new PapirSoknadEngangstonadBekreftelse());
    }

    private FordelingDto lagFordelingDto() {
        return new FordelingDto(
                List.of(lagPermisjonPeriodeDto()),
                List.of(lagGraderingPeriodeDto()),
                List.of(lagUtsettelsePeriodeDto()));
    }

    private PermisjonPeriodeDto lagPermisjonPeriodeDto() {
        if (permisjonPeriodeDto != null) {
            return permisjonPeriodeDto;
        }
        permisjonPeriodeDto = new PermisjonPeriodeDto(FELLESPERIODE, LocalDate.now(), LocalDate.now().plusMonths(21));
        return permisjonPeriodeDto;

    }

    private GraderingPeriodeDto lagGraderingPeriodeDto() {
        if (graderingPeriodeDto != null) {
            return graderingPeriodeDto;
        }
        graderingPeriodeDto = new GraderingPeriodeDto(FELLESPERIODE, LocalDate.now(), LocalDate.now().plusYears(1),
                BigDecimal.TEN,"AG-ID-123456", true, false,
                false);
        return graderingPeriodeDto;
    }

    private UtsettelsePeriodeDto lagUtsettelsePeriodeDto() {
        if (utsettelsePeriodeDto != null) {
            return utsettelsePeriodeDto;
        }
        utsettelsePeriodeDto = new UtsettelsePeriodeDto(LocalDate.now().minusYears(1), LocalDate.now(), ARBEID);
        return utsettelsePeriodeDto;
    }
}
