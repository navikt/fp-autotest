package no.nav.foreldrepenger.generator.soknad.maler;

import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.uttaksplan.UttaksplanDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.uttaksplan.Uttaksplanperiode;

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

    public static List<Uttaksplanperiode> fordelingEndringssøknadGradering(StønadskontoType stønadskonto, LocalDate fom, LocalDate tom, ArbeidsgiverIdentifikator identifikator, Integer arbeidstidsprosentIOrgnr) {
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
