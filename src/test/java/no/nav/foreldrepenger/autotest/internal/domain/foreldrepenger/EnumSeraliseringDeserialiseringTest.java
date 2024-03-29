package no.nav.foreldrepenger.autotest.internal.domain.foreldrepenger;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.InnsynResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.MedlemskapManuellVurderingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakresultatUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.VurderÅrsak;
import no.nav.foreldrepenger.autotest.internal.SerializationTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.FagsakStatus;

@Execution(ExecutionMode.SAME_THREAD)
@Tag("internal")
class EnumSeraliseringDeserialiseringTest extends SerializationTestBase {

    @Test
    void AvslagsårsakTest() {
        test(Avslagsårsak.BARN_OVER_15_ÅR);
    }

    @Test
    void BehandlingResultatTypeTest() {
        test(BehandlingResultatType.INNVILGET);
    }

    @Test
    void BehandlingTypeTest() {
        test(BehandlingType.FØRSTEGANGSSØKNAD);
    }


    @Test
    void BehandlingÅrsakTypeTest() {
        test(BehandlingÅrsakType.ETTER_KLAGE);
    }

    @Test
    void FagsakStatusTest() {
        test(FagsakStatus.LØPENDE);
    }

    @Test
    void InnsynResultatTypeTest() {
        test(InnsynResultatType.INNVILGET);
    }

    @Test
    void InntektskategoriTest() {
        test(Inntektskategori.ARBEIDSTAKER);
    }

    @Test
    void PeriodeResultatÅrsakTest() {
        test(PeriodeResultatÅrsak.FORELDREPENGER_ALENEOMSORG);
        test(PeriodeResultatÅrsak.AKTIVITETSKRAVET_ARBEID_IKKE_DOKUMENTERT);
    }

    @Test
    void MedlemskapManuellVurderingTypeTest() {
        test(MedlemskapManuellVurderingType.MEDLEM);
    }

    @Test
    void OmsorgsovertakelseVilkårTypeTest() {
        test(OmsorgsovertakelseVilkårType.FORELDREANSVARSVILKÅRET_2_LEDD);
    }

    @Test
    void UttakUtsettelseÅrsakTest() {
        test(UttakresultatUtsettelseÅrsak.BARN_INNLAGT);
    }

    @Test
    void VurderÅrsakTest() {
        test(VurderÅrsak.FEIL_FAKTA);
    }
}
