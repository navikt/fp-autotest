package no.nav.foreldrepenger.generator.familie;

import java.util.Comparator;

import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.sigrun.Inntektsår;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.sigrun.SigrunModell;

final class Sigrun {

    private Sigrun() {
    }

    static double hentNæringsinntekt(SigrunModell sigrunModell, int beregnFraOgMedÅr) {
        double gjennomsnittDeTreSisteÅrene = sigrunModell.inntektsår().stream()
                .sorted(Comparator.comparing(Inntektsår::år).reversed())
                .filter(inntektsår -> Integer.parseInt(inntektsår.år()) <= beregnFraOgMedÅr)
                .flatMap(inntektsår -> inntektsår.oppføring().stream())
                .filter(oppføring -> oppføring.tekniskNavn().equals("personinntektNaering")) // For å hente skattegrunlag, ikke oppgjørsdato
                .mapToDouble(oppføring -> Double.parseDouble(oppføring.verdi()))
                .limit(3)
                .sum();
        return gjennomsnittDeTreSisteÅrene/3;
    }

    static Integer startdato(SigrunModell modell) {
        return modell.inntektsår().stream()
                .sorted(Comparator.comparing(Inntektsår::år))
                .map(inntektsår -> Integer.parseInt(inntektsår.år()))
                .findFirst()
                .orElseThrow();
    }
}
