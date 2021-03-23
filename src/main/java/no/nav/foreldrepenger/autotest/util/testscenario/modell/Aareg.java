package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.ArbeidsforholdModell;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforholdstype;

final class Aareg {

    private Aareg() {
    }

    static List<Arbeidsforhold> arbeidsforholdene(ArbeidsforholdModell aareg) {
        return aareg.arbeidsforhold().stream()
                .map(Aareg::mapTilArbeidsforhold)
                .collect(Collectors.toList());
    }

    static List<Arbeidsforhold> arbeidsforholdene(ArbeidsforholdModell aareg, Orgnummer orgnummer) {
        return aareg.arbeidsforhold().stream()
                .filter(a -> a.arbeidsgiverOrgnr().equalsIgnoreCase(orgnummer.orgnummer()))
                .map(Aareg::mapTilArbeidsforhold)
                .collect(Collectors.toList());
    }

    static List<Arbeidsforhold> arbeidsforholdFrilans(ArbeidsforholdModell aareg) {
        return aareg.arbeidsforhold().stream()
                .filter(a -> a.arbeidsforholdstype().equals(Arbeidsforholdstype.FRILANSER_OPPDRAGSTAKER_MED_MER))
                .map(Aareg::mapTilArbeidsforhold)
                .collect(Collectors.toList());
    }

    private static Arbeidsforhold mapTilArbeidsforhold(no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold a) {
        return new Arbeidsforhold(new Orgnummer(a.arbeidsgiverOrgnr()), new ArbeidsforholdId(a.arbeidsforholdId()), a.ansettelsesperiodeFom(),
                a.ansettelsesperiodeTom(), a.arbeidsforholdstype(), a.arbeidsavtaler().get(0).stillingsprosent());
    }
}
