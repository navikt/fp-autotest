package no.nav.foreldrepenger.generator.familie;

import java.util.Comparator;

import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
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

    static int månedsinntekt(InntektskomponentModell inntektskomponenten, Orgnummer identifikator) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> erArbeidsgiver(identifikator.value(), p))
                .max(Comparator.comparing(Inntektsperiode::tom))
                .map(Inntektsperiode::beløp)
                .orElse(0);
    }

    static int månedsinntekt(InntektskomponentModell inntektskomponenten, String identifikator) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> erArbeidsgiver(identifikator, p))
                .max(Comparator.comparing(Inntektsperiode::tom))
                .map(Inntektsperiode::beløp)
                .orElse(0);
    }

    private static boolean erArbeidsgiver(String identifikator, Inntektsperiode p) {
        if (p.orgnr() != null) {
            return identifikator.equalsIgnoreCase(p.orgnr());
        }
        if (p.arbeidsgiver() != null) {
            return identifikator.equalsIgnoreCase(p.arbeidsgiver().getAktørIdent());
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
