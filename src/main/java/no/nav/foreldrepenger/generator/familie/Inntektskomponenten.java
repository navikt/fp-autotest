package no.nav.foreldrepenger.generator.familie;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.vtp.kontrakter.v2.InntektkomponentDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.InntektsperiodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PrivatArbeidsgiver;
import no.nav.foreldrepenger.vtp.kontrakter.v2.TilordnetIdentDto;

final class Inntektskomponenten {

    private Inntektskomponenten() {
    }

    static int månedsinntekt(InntektkomponentDto inntektskomponenten) {
        return inntektskomponenten.inntektsperioder().stream()
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    static int månedsinntekt(InntektkomponentDto inntektskomponenten, Map<UUID, TilordnetIdentDto> identer, Orgnummer identifikator) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> erArbeidsgiver(identifikator.value(), identer, p))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    static int månedsinntekt(InntektkomponentDto inntektskomponenten, Map<UUID, TilordnetIdentDto> identer, String identifikator) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> erArbeidsgiver(identifikator, identer, p))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    private static boolean erArbeidsgiver(String identifikator, Map<UUID, TilordnetIdentDto> identer, InntektsperiodeDto p) {
        var aident = Arbeidsgiver.hentIdentifikator(p.arbeidsgiver(), identer);
        return Objects.equals(aident, identifikator);
    }

    static int månedsinntekt(InntektkomponentDto inntektskomponenten, Map<UUID, TilordnetIdentDto> identer, Fødselsnummer arbeidsgiverFnr) {
        return inntektskomponenten.inntektsperioder().stream()
                .filter(p -> p.arbeidsgiver() instanceof PrivatArbeidsgiver)
                .filter(p -> arbeidsgiverFnr.value().equalsIgnoreCase(identer.get(((PrivatArbeidsgiver) p.arbeidsgiver()).uuid()).aktørId()))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }
}
