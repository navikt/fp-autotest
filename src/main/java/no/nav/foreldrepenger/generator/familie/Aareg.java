package no.nav.foreldrepenger.generator.familie;


import java.util.List;
import java.util.Map;
import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.vtp.kontrakter.person.TilordnetIdentDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.Arbeidsforholdstype;

final class Aareg {

    private Aareg() {
    }

    static List<Arbeidsforhold> arbeidsforholdene(List<ArbeidsforholdDto> arbeidsforhold, Map<UUID, TilordnetIdentDto> identer) {
        return arbeidsforhold.stream()
                .map(a -> mapTilArbeidsforhold(a, identer))
                .toList();
    }

    static List<Arbeidsforhold> arbeidsforholdene(List<ArbeidsforholdDto> arbeidsforhold, Map<UUID, TilordnetIdentDto> identer, Orgnummer orgnummer) {
        return arbeidsforholdene(arbeidsforhold, identer, orgnummer.value());
    }

    static List<Arbeidsforhold> arbeidsforholdene(List<ArbeidsforholdDto> arbeidsforhold, Map<UUID, TilordnetIdentDto> identer, AktørId aktørId) {
        return arbeidsforholdene(arbeidsforhold, identer, aktørId.value());
    }

    static List<Arbeidsforhold> arbeidsforholdene(List<ArbeidsforholdDto> arbeidsforhold, Map<UUID, TilordnetIdentDto> identer, String identifikator) {
        return arbeidsforhold.stream()
                .filter(a -> erArbeidsgiver(identifikator, a, identer))
                .map(a -> mapTilArbeidsforhold(a, identer))
                .toList();
    }

    static List<Arbeidsforhold> arbeidsforholdFrilans(List<ArbeidsforholdDto> arbeidsforhold, Map<UUID, TilordnetIdentDto> identer) {
        return arbeidsforhold.stream()
                .filter(a -> a.arbeidsforholdstype().equals(Arbeidsforholdstype.FRILANSER_OPPDRAGSTAKER_MED_MER))
                .map(a -> mapTilArbeidsforhold(a, identer))
                .toList();
    }

    private static Arbeidsforhold mapTilArbeidsforhold(ArbeidsforholdDto a, Map<UUID, TilordnetIdentDto> identer) {
        return new Arbeidsforhold(
                Arbeidsgiver.hentIdentifikator(a.arbeidsgiver(), identer),
                new ArbeidsforholdId(a.arbeidsforholdId()), a.ansettelsesperiodeFom(), a.ansettelsesperiodeTom(),
                a.arbeidsforholdstype(), a.arbeidsavtaler().getFirst().stillingsprosent());
    }

    private static boolean erArbeidsgiver(String identifikator, ArbeidsforholdDto a, Map<UUID, TilordnetIdentDto> identer) {
        var aident = Arbeidsgiver.hentIdentifikator(a.arbeidsgiver(), identer);
        if (identifikator == null || aident == null) {
            return false;
        }
        return aident.equals(identifikator);
    }
}
