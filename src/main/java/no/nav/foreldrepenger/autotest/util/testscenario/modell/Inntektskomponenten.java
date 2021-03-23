package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.util.Comparator;

import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.InntektskomponentModell;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.inntektkomponent.Inntektsperiode;

final class Inntektskomponenten {

    private Inntektskomponenten() {
    }

    static int månedsinntekt(InntektskomponentModell inntektskomponenten) {
        return inntektskomponenten.inntektsperioder().stream()
                .max(Comparator.comparing(Inntektsperiode::tom))
                .map(Inntektsperiode::beløp)
                .orElse(0);
    }

    static int månedsinntekt(InntektskomponentModell inntektskomponenten, Orgnummer orgnummer) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> orgnummer.orgnummer().equalsIgnoreCase(p.orgnr()))
                .max(Comparator.comparing(Inntektsperiode::tom))
                .map(Inntektsperiode::beløp)
                .orElse(0);
    }

}
