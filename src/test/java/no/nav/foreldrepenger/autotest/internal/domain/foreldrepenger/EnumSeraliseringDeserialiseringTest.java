package no.nav.foreldrepenger.autotest.internal.domain.foreldrepenger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OppholdÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class EnumSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void FagsakTest() {
        test(OppholdÅrsak.FELLESPERIODE_ANNEN_FORELDER);
        test(OppholdÅrsak.UDEFINERT);
    }

    @Test
    void StønadskontoTest() {
        test(Stønadskonto.FEDREKVOTE);
        test(Stønadskonto.INGEN_STØNADSKONTO);
    }

    @Test
    void SøknadUtsettelseÅrsakTest() {
        test(SøknadUtsettelseÅrsak.ARBEID);
        test(SøknadUtsettelseÅrsak.UDEFINERT);
    }

    @Test
    void UttakUtsettelseÅrsakTest() {
        test(UttakUtsettelseÅrsak.BARN_INNLAGT);
        test(UttakUtsettelseÅrsak.UDEFINERT);
    }

}
