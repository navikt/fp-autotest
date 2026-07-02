package no.nav.foreldrepenger.generator.familie;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.vtp.kontrakter.person.TilordnetIdentDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.InntektsperiodeDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PrivatArbeidsgiverDto;

final class Inntektskomponenten {

    private Inntektskomponenten() {
    }

    static int månedsinntekt(List<InntektsperiodeDto> inntektsperioder) {
        return inntektsperioder == null || inntektsperioder.isEmpty() ? 0 : inntektsperioder.stream()
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    static int månedsinntekt(List<InntektsperiodeDto> inntektsperioder, Map<UUID, TilordnetIdentDto> identer, Orgnummer identifikator) {
        return inntektsperioder == null ? 0 : inntektsperioder.stream()
                .filter(p -> erArbeidsgiver(identifikator.value(), identer, p))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    static int månedsinntekt(List<InntektsperiodeDto> inntektsperioder, Map<UUID, TilordnetIdentDto> identer, String identifikator) {
        return inntektsperioder == null ? 0 : inntektsperioder.stream()
                .filter(p -> erArbeidsgiver(identifikator, identer, p))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }

    private static boolean erArbeidsgiver(String identifikator, Map<UUID, TilordnetIdentDto> identer, InntektsperiodeDto p) {
        var aident = Arbeidsgiver.hentIdentifikator(p.arbeidsgiver(), identer);
        return Objects.equals(aident, identifikator);
    }

    static int månedsinntekt(List<InntektsperiodeDto> inntektsperioder, Map<UUID, TilordnetIdentDto> identer, Fødselsnummer arbeidsgiverFnr) {
        return inntektsperioder == null ? 0 : inntektsperioder.stream()
                .filter(p -> p.arbeidsgiver() instanceof PrivatArbeidsgiverDto)
                .filter(p -> arbeidsgiverFnr.value().equalsIgnoreCase(identer.get(((PrivatArbeidsgiverDto) p.arbeidsgiver()).uuid()).aktørId()))
                .max(Comparator.comparing(InntektsperiodeDto::tom))
                .map(InntektsperiodeDto::beløp)
                .orElse(0);
    }
}
