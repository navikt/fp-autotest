package no.nav.foreldrepenger.generator.familie;


import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.arbeidsforhold.Arbeidsforholdstype;

final class Aareg {

    private Aareg() {
    }

    static List<Arbeidsforhold> arbeidsforholdene(List<ArbeidsforholdDto> arbeidsforholdDtoer) {
        return Optional.ofNullable(arbeidsforholdDtoer).orElseGet(List::of).stream()
                .map(Aareg::mapTilArbeidsforhold)
                .toList();
    }

    static List<Arbeidsforhold> arbeidsforholdene(List<ArbeidsforholdDto> arbeidsforholdDtoer, Orgnummer orgnummer) {
        return arbeidsforholdene(arbeidsforholdDtoer, orgnummer.value());
    }

    static List<Arbeidsforhold> arbeidsforholdene(List<ArbeidsforholdDto> arbeidsforholdDtoer, AktørId aktørId) {
        return arbeidsforholdene(arbeidsforholdDtoer, aktørId.value());
    }

    static List<Arbeidsforhold> arbeidsforholdene(List<ArbeidsforholdDto> arbeidsforholdDtoer, String identifikator) {
        return Optional.ofNullable(arbeidsforholdDtoer).orElseGet(List::of).stream()
                .filter(a -> erArbeidsgiver(identifikator, a))
                .map(Aareg::mapTilArbeidsforhold)
                .toList();
    }


    static List<Arbeidsforhold> arbeidsforholdFrilans(List<ArbeidsforholdDto> arbeidsforholdDtoer) {
        return Optional.ofNullable(arbeidsforholdDtoer).orElseGet(List::of).stream()
                .filter(a -> a.arbeidsforholdstype().equals(Arbeidsforholdstype.FRILANSER_OPPDRAGSTAKER_MED_MER))
                .map(Aareg::mapTilArbeidsforhold)
                .toList();
    }

    private static Arbeidsforhold mapTilArbeidsforhold(ArbeidsforholdDto a) {
        return new Arbeidsforhold(
                Arbeidsgiver.hentIdentifikator(a.arbeidsgiver()),
                new ArbeidsforholdId(a.arbeidsforholdId()), a.ansettelsesperiodeFom(), a.ansettelsesperiodeTom(),
                a.arbeidsforholdstype(), a.arbeidsavtaler().getFirst().stillingsprosent());
    }

    private static boolean erArbeidsgiver(String identifikator, ArbeidsforholdDto a) {
        var aident = Arbeidsgiver.hentIdentifikator(a.arbeidsgiver());
        if (identifikator == null || aident == null) {
            return false;
        }
        return aident.equals(identifikator);
    }
}
