package no.nav.foreldrepenger.generator.familie;

import java.util.Comparator;

import no.nav.foreldrepenger.vtp.kontrakter.v2.SigrunDto;


final class Sigrun {

    private Sigrun() {
    }

    static double hentNæringsinntekt(SigrunDto sigrunModell, int beregnFraOgMedÅr) {
        double gjennomsnittDeTreSisteÅrene = sigrunModell.inntektår().stream()
                .sorted(Comparator.comparing(SigrunDto.InntektsårDto::år).reversed())
                .filter(inntektsår -> inntektsår.år() <= beregnFraOgMedÅr)
                .mapToDouble(oppføring -> oppføring.beløp().doubleValue())
                .limit(3)
                .sum();
        return gjennomsnittDeTreSisteÅrene/3;
    }

    static Integer startdato(SigrunDto modell) {
        return modell.inntektår().stream()
                .sorted(Comparator.comparing(SigrunDto.InntektsårDto::år))
                .map(SigrunDto.InntektsårDto::år)
                .findFirst()
                .orElseThrow();
    }
}
