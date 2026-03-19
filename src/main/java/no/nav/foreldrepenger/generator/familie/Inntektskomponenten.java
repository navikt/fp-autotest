package no.nav.foreldrepenger.generator.familie;

import java.util.Comparator;
import java.util.Objects;

import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.vtp.kontrakter.person.InntektkomponentDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.InntektsperiodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.PrivatArbeidsgiver;

final class Inntektskomponenten {

    private Inntektskomponenten() {
    }

    static int månedsinntekt(InntektkomponentDto inntektskomponenten) {
        return inntektskomponenten.inntektsperioder().stream()
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    static int månedsinntekt(InntektkomponentDto inntektskomponenten, Orgnummer identifikator) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> erArbeidsgiver(identifikator.value(), p))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    static int månedsinntekt(InntektkomponentDto inntektskomponenten, String identifikator) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> erArbeidsgiver(identifikator, p))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    private static boolean erArbeidsgiver(String identifikator, InntektsperiodeDto p) {
        var aident = Arbeidsgiver.hentIdentifikator(p.arbeidsgiver());
        return Objects.equals(aident, identifikator);
    }

    static int månedsinntekt(InntektkomponentDto inntektskomponenten, Fødselsnummer arbeidsgiverFnr) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> p.arbeidsgiver() instanceof PrivatArbeidsgiver)
                .filter(p -> arbeidsgiverFnr.value().equalsIgnoreCase(((PrivatArbeidsgiver) p.arbeidsgiver()).fnr()))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }
}
