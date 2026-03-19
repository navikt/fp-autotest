package no.nav.foreldrepenger.generator.familie;


import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.vtp.kontrakter.person.AaregDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.ArbeidsforholdDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.Arbeidsforholdstype;

final class Aareg {

    private Aareg() {
    }

    static List<Arbeidsforhold> arbeidsforholdene(AaregDto aareg) {
        return Optional.ofNullable(aareg).map(AaregDto::arbeidsforhold).orElseGet(List::of).stream()
                .map(Aareg::mapTilArbeidsforhold)
                .toList();
    }

    static List<Arbeidsforhold> arbeidsforholdene(AaregDto aareg, Orgnummer orgnummer) {
        return arbeidsforholdene(aareg, orgnummer.value());
    }

    static List<Arbeidsforhold> arbeidsforholdene(AaregDto aareg, AktørId aktørId) {
        return arbeidsforholdene(aareg, aktørId.value());
    }

    static List<Arbeidsforhold> arbeidsforholdene(AaregDto aareg, String identifikator) {
        return Optional.ofNullable(aareg).map(AaregDto::arbeidsforhold).orElseGet(List::of).stream()
                .filter(a -> erArbeidsgiver(identifikator, a))
                .map(Aareg::mapTilArbeidsforhold)
                .toList();
    }


    static List<Arbeidsforhold> arbeidsforholdFrilans(AaregDto aareg) {
        return Optional.ofNullable(aareg).map(AaregDto::arbeidsforhold).orElseGet(List::of).stream()
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
