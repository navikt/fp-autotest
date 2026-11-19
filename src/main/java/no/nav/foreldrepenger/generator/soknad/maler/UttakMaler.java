package no.nav.foreldrepenger.generator.soknad.maler;

import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FORELDREPENGER;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType.MØDREKVOTE;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.soknad.kontrakt.BrukerRolle;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Rolle;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Gradering;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakDto;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.UttakPeriodeDto;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.UttaksplanDto;

/**
 * Fordeling == Uttaksplan
 */
public final class UttakMaler {

    private UttakMaler() {
    }

    public static List<UttakPeriodeDto> fordelingHappyCase(LocalDate familehendelseDato, BrukerRolle søkerRolle) {
        return switch (søkerRolle) {
            case MOR -> fordelingMorHappyCaseLong(familehendelseDato);
            case FAR, MEDMOR -> fordelingFarHappyCase(familehendelseDato);
        };
    }

    public static List<UttakPeriodeDto> fordelingMorHappyCaseLong(LocalDate familehendelseDato) {
        return List.of(
                uttaksperiode(Rolle.MOR, FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(3), familehendelseDato.minusDays(1)),
                uttaksperiode(Rolle.MOR, MØDREKVOTE, familehendelseDato, familehendelseDato.plusWeeks(15).minusDays(1)),
                uttaksperiode(Rolle.MOR, FELLESPERIODE, familehendelseDato.plusWeeks(15), familehendelseDato.plusWeeks(31).minusDays(1))
        );
    }

    public static List<UttakPeriodeDto> fordelingFarHappyCase(LocalDate familehendelseDato) {
        return List.of(
                uttaksperiode(Rolle.FAR_MEDMOR, FELLESPERIODE, familehendelseDato.plusWeeks(3), familehendelseDato.plusWeeks(5))
        );
    }

    public static List<UttakPeriodeDto> fordelingFarAleneomsorg(LocalDate familehendelseDato) {
        return List.of(
                uttaksperiode(Rolle.FAR_MEDMOR, FORELDREPENGER, familehendelseDato, familehendelseDato.plusWeeks(20))
        );
    }

    public static List<UttakPeriodeDto> fordelingMorAleneomsorgHappyCase(LocalDate familehendelseDato) {
        return List.of(
                uttaksperiode(Rolle.MOR, FORELDREPENGER_FØR_FØDSEL, familehendelseDato.minusWeeks(3), familehendelseDato.minusDays(1)),
                uttaksperiode(Rolle.MOR, FORELDREPENGER, familehendelseDato, familehendelseDato.plusWeeks(100))
        );
    }

    public static UttaksplanDto fordeling(UttakPeriodeDto... perioder) {
        return fordeling(List.of(perioder));
    }

    // Søknaden sender ønskerJustertUttakVedFødsel bare når spørsmålet er besvart (far/medmor ved termin).
    public static UttaksplanDto fordeling(List<UttakPeriodeDto> perioder) {
        return new UttaksplanDto(null, perioder);
    }

    /**
     * Returnerer en ny plan med søker og annen part byttet. EØS-uttak fjernes siden det gjelder opprinnelig annen part.
     * Perioder med bare EØS-uttak tas ut av planen. Søkerens uttak blir annen parts uttak via {@link #somAnnenPart}.
     */
    public static List<UttakPeriodeDto> byttPerspektiv(List<UttakPeriodeDto> plan) {
        return plan.stream()
                .filter(p -> p.søker() != null || p.annenPart() != null || p.annenPartEøs() == null)
                .map(p -> new UttakPeriodeDto(p.fom(), p.tom(), p.annenPart(), somAnnenPart(p.søker()), null))
                .toList();
    }

    /**
     * Planen slik søkeren sender den: annen parts gradering uten aktivitet (se {@link #somAnnenPart}).
     */
    public static List<UttakPeriodeDto> utenAnnenPartsAktivitet(List<UttakPeriodeDto> plan) {
        return plan.stream()
                .map(p -> new UttakPeriodeDto(p.fom(), p.tom(), p.søker(), somAnnenPart(p.annenPart()), p.annenPartEøs()))
                .toList();
    }

    /**
     * Uttaket slik det ser ut som annen parts uttak: graderingen har ikke aktivitet. Søknaden lar ikke søkeren oppgi
     * aktivitet for annen part, og fp-oversikt returnerer annen parts gradering uten aktivitet.
     */
    public static UttakDto somAnnenPart(UttakDto uttak) {
        if (uttak == null || uttak.gradering() == null || uttak.gradering().aktivitet() == null) {
            return uttak;
        }
        return new UttakDto(uttak.forelder(), uttak.kontoType(), uttak.utsettelseÅrsak(), uttak.overføringÅrsak(),
                new Gradering(uttak.gradering().arbeidstidprosent(), null), uttak.morsAktivitet(), uttak.samtidigUttak(),
                uttak.flerbarnsdager(), uttak.resultat());
    }

}
