package no.nav.foreldrepenger.generator.familie;

import java.util.Comparator;
import java.util.List;

import no.nav.foreldrepenger.vtp.kontrakter.person.skatt.SkatteopplysningDto;


final class Sigrun {

    private Sigrun() {
    }

    static double hentNæringsinntekt(List<SkatteopplysningDto> skatteopplysninger, int beregnFraOgMedÅr) {
        double gjennomsnittDeTreSisteÅrene = skatteopplysninger.stream()
                .sorted(Comparator.comparing(SkatteopplysningDto::år).reversed())
                .filter(inntektsår -> inntektsår.år() <= beregnFraOgMedÅr)
                .mapToDouble(oppføring -> oppføring.beløp().doubleValue())
                .limit(3)
                .sum();
        return gjennomsnittDeTreSisteÅrene/3;
    }

    static Integer startdato(List<SkatteopplysningDto> skatteopplysninger) {
        return skatteopplysninger.stream()
                .sorted(Comparator.comparing(SkatteopplysningDto::år))
                .map(SkatteopplysningDto::år)
                .findFirst()
                .orElseThrow();
    }
}
