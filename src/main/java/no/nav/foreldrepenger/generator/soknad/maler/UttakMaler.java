package no.nav.foreldrepenger.generator.soknad.maler;

import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType.MØDREKVOTE;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.kontrakter.fpsoknad.BrukerRolle;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.UttaksplanDto;
import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.Uttaksplanperiode;

/**
 * Fordeling == Uttaksplan
 */
public final class UttakMaler {

    private UttakMaler() {
    }

    public static List<Uttaksplanperiode> fordelingHappyCase(LocalDate familehendelseDato, BrukerRolle søkerRolle) {
        if (søkerRolle == BrukerRolle.MOR) {
            return fordelingMorHappyCaseLong(familehendelseDato);
        }
        return fordelingFarHappyCase(familehendelseDato);
    }

    public static List<Uttaksplanperiode> fordelingMorHappyCase(LocalDate familehendelseDato) {
        return List.of(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(3), familehendelseDato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, familehendelseDato, familehendelseDato.plusWeeks(10))
        );
    }


    public static List<Uttaksplanperiode> fordelingMorHappyCaseLong(LocalDate familehendelseDato) {
        return List.of(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(3), familehendelseDato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, familehendelseDato, familehendelseDato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, familehendelseDato.plusWeeks(15), familehendelseDato.plusWeeks(31).minusDays(1))
        );
    }

    public static List<Uttaksplanperiode> fordelingFarHappyCase(LocalDate familehendelseDato) {
        return List.of(
                uttaksperiode(FELLESPERIODE, familehendelseDato.plusWeeks(3), familehendelseDato.plusWeeks(5))
        );
    }

    public static List<Uttaksplanperiode> fordelingEndringssøknadGradering(KontoType stønadskonto, LocalDate fom, LocalDate tom, String identifikator, Integer arbeidstidsprosentIOrgnr) {
        return List.of(
                graderingsperiodeArbeidstaker(stønadskonto, fom, tom, identifikator, arbeidstidsprosentIOrgnr)
        );
    }

    public static List<Uttaksplanperiode> fordelingFarAleneomsorg(LocalDate familehendelseDato) {
        return List.of(
                uttaksperiode(FORELDREPENGER, familehendelseDato, familehendelseDato.plusWeeks(20))
        );
                //.erAnnenForelderInformert(false); // TODO
    }

    public static List<Uttaksplanperiode> fordelingMorAleneomsorgHappyCase(LocalDate familehendelseDato) {
        return List.of(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(3), familehendelseDato.minusDays(1)),
                uttaksperiode(FORELDREPENGER, familehendelseDato, familehendelseDato.plusWeeks(100))
        );
                // .erAnnenForelderInformert(false); // TODO
    }

    public static UttaksplanDto fordeling(Uttaksplanperiode... perioder) {
        return new UttaksplanDto(null, List.of(perioder));
    }
}
