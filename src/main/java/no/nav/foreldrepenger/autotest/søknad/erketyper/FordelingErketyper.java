package no.nav.foreldrepenger.autotest.søknad.erketyper;

import static no.nav.foreldrepenger.autotest.søknad.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.StønadskontoType.MØDREKVOTE;

import java.time.LocalDate;
import java.util.Arrays;

import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.Fordeling;
import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling.LukketPeriodeMedVedlegg;

public class FordelingErketyper {

    public static Fordeling fordelingHappyCase(LocalDate familehendelseDato, BrukerRolle søkerRolle) {
        if (søkerRolle == BrukerRolle.MOR) {
            return fordelingMorHappyCaseLong(familehendelseDato);
        } else {
            return fordelingFarHappyCase(familehendelseDato);
        }
    }

    public static Fordeling fordelingMorHappyCase(LocalDate familehendelseDato) {
        return generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(3),
                        familehendelseDato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, familehendelseDato, familehendelseDato.plusWeeks(10)));
    }


    public static Fordeling fordelingMorHappyCaseLong(LocalDate familehendelseDato) {
        return generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(3),
                        familehendelseDato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, familehendelseDato, familehendelseDato.plusWeeks(15).minusDays(1)),
                uttaksperiode(FELLESPERIODE, familehendelseDato.plusWeeks(15),
                        familehendelseDato.plusWeeks(31).minusDays(1)));
    }

    public static Fordeling fordelingFarHappyCase(LocalDate familehendelseDato) {
        return generiskFordeling(
                uttaksperiode(FELLESPERIODE, familehendelseDato.plusWeeks(3), familehendelseDato.plusWeeks(5)));
    }

    public static Fordeling generiskFordeling(LukketPeriodeMedVedlegg... perioder) {
        return Fordeling.builder()
                .erAnnenForelderInformert(true)
                .perioder(Arrays.asList(perioder))
                .build();
    }

}
