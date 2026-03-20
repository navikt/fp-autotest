package no.nav.foreldrepenger.generator.familie;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.PrivatArbeidsgiverDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.inntekt.InntektsperiodeDto;

final class Inntektskomponenten {

    private Inntektskomponenten() {
    }

    static int månedsinntekt(List<InntektsperiodeDto> inntektsperioder) {
        return inntektsperioder.stream()
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    static int månedsinntekt(List<InntektsperiodeDto> inntektsperioder, Orgnummer identifikator) {
        return inntektsperioder.stream()
                .filter(p -> erArbeidsgiver(identifikator.value(), p))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    static int månedsinntekt(List<InntektsperiodeDto> inntektsperioder, String identifikator) {
        return inntektsperioder.stream()
                .filter(p -> erArbeidsgiver(identifikator, p))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    private static boolean erArbeidsgiver(String identifikator, InntektsperiodeDto p) {
        var aident = Arbeidsgiver.hentIdentifikator(p.arbeidsgiver());
        return Objects.equals(aident, identifikator);
    }

    static int månedsinntekt(List<InntektsperiodeDto> inntektsperioder, Fødselsnummer arbeidsgiverFnr) {
        return inntektsperioder.stream()
                .filter(p -> p.arbeidsgiver() instanceof PrivatArbeidsgiverDto)
                .filter(p -> arbeidsgiverFnr.value().equalsIgnoreCase(((PrivatArbeidsgiverDto) p.arbeidsgiver()).fnr()))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }
}
