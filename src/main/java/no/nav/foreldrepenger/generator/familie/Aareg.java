package no.nav.foreldrepenger.generator.familie;


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.vtp.kontrakter.v2.AaregDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.Arbeidsforholdstype;
import no.nav.foreldrepenger.vtp.kontrakter.v2.TilordnetIdentDto;

final class Aareg {

    private Aareg() {
    }

    static List<Arbeidsforhold> arbeidsforholdene(AaregDto aareg, Map<UUID, TilordnetIdentDto> identer) {
        return Optional.ofNullable(aareg).map(AaregDto::arbeidsforhold).orElseGet(List::of).stream()
                .map(a -> mapTilArbeidsforhold(a, identer))
                .toList();
    }

    static List<Arbeidsforhold> arbeidsforholdene(AaregDto aareg, Map<UUID, TilordnetIdentDto> identer, Orgnummer orgnummer) {
        return arbeidsforholdene(aareg, identer, orgnummer.value());
    }

    static List<Arbeidsforhold> arbeidsforholdene(AaregDto aareg, Map<UUID, TilordnetIdentDto> identer, AktørId aktørId) {
        return arbeidsforholdene(aareg, identer, aktørId.value());
    }

    static List<Arbeidsforhold> arbeidsforholdene(AaregDto aareg, Map<UUID, TilordnetIdentDto> identer, String identifikator) {
        return Optional.ofNullable(aareg).map(AaregDto::arbeidsforhold).orElseGet(List::of).stream()
                .filter(a -> erArbeidsgiver(identifikator, a, identer))
                .map(a -> mapTilArbeidsforhold(a, identer))
                .toList();
    }


    static List<Arbeidsforhold> arbeidsforholdFrilans(AaregDto aareg, Map<UUID, TilordnetIdentDto> identer) {
        return Optional.ofNullable(aareg).map(AaregDto::arbeidsforhold).orElseGet(List::of).stream()
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
