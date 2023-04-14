package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.util.Comparator;

import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
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

    static int månedsinntekt(InntektskomponentModell inntektskomponenten, ArbeidsgiverIdentifikator arbeidsgiverIdentifikator) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> erArbeidsgiver(arbeidsgiverIdentifikator, p))
                .max(Comparator.comparing(Inntektsperiode::tom))
                .map(Inntektsperiode::beløp)
                .orElse(0);
    }

    private static boolean erArbeidsgiver(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator, Inntektsperiode p) {
        if (p.orgnr() != null) {
            return arbeidsgiverIdentifikator.value().equalsIgnoreCase(p.orgnr());
        }
        if (p.arbeidsgiver() != null) {
            return arbeidsgiverIdentifikator.value().equalsIgnoreCase(p.arbeidsgiver().getAktørIdent());
        }
        return false;
    }

    static int månedsinntekt(InntektskomponentModell inntektskomponenten, Fødselsnummer arbeidsgiverFnr) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> p.arbeidsgiver() != null)
                .filter(p -> arbeidsgiverFnr.value().equalsIgnoreCase(p.arbeidsgiver().getIdent()))
                .max(Comparator.comparing(Inntektsperiode::tom))
                .map(Inntektsperiode::beløp)
                .orElse(0);
    }
}
