package no.nav.foreldrepenger.generator.familie;


import static no.nav.foreldrepenger.autotest.util.StreamUtils.safeStream;

import java.util.List;

import no.nav.foreldrepenger.kontrakter.felles.typer.AktørId;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.ArbeidsforholdModell;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforholdstype;

final class Aareg {

    private Aareg() {
    }

    static List<Arbeidsforhold> arbeidsforholdene(ArbeidsforholdModell aareg) {
        return safeStream(aareg.arbeidsforhold())
                .map(Aareg::mapTilArbeidsforhold)
                .toList();
    }

    static List<Arbeidsforhold> arbeidsforholdene(ArbeidsforholdModell aareg, Orgnummer orgnummer) {
        return arbeidsforholdene(aareg, orgnummer.value());
    }

    static List<Arbeidsforhold> arbeidsforholdene(ArbeidsforholdModell aareg, AktørId aktørId) {
        return arbeidsforholdene(aareg, aktørId.value());
    }

    static List<Arbeidsforhold> arbeidsforholdene(ArbeidsforholdModell aareg, String identifikator) {
        return safeStream(aareg.arbeidsforhold())
                .filter(a -> erArbeidsgiver(identifikator, a))
                .map(Aareg::mapTilArbeidsforhold)
                .toList();
    }


    static List<Arbeidsforhold> arbeidsforholdFrilans(ArbeidsforholdModell aareg) {
        return aareg.arbeidsforhold().stream()
                .filter(a -> a.arbeidsforholdstype().equals(Arbeidsforholdstype.FRILANSER_OPPDRAGSTAKER_MED_MER))
                .map(Aareg::mapTilArbeidsforhold)
                .toList();
    }

    private static Arbeidsforhold mapTilArbeidsforhold(no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold a) {
        return new Arbeidsforhold(
                a.arbeidsgiverOrgnr() != null ? a.arbeidsgiverOrgnr() : a.arbeidsgiverAktorId(),
                new ArbeidsforholdId(a.arbeidsforholdId()), a.ansettelsesperiodeFom(), a.ansettelsesperiodeTom(),
                a.arbeidsforholdstype(), a.arbeidsavtaler().getFirst().stillingsprosent());
    }

    private static boolean erArbeidsgiver(String identifikator, no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold a) {
        if (identifikator == null) {
            return false;
        }
        if (a.arbeidsgiverOrgnr() != null) {
             return identifikator.equalsIgnoreCase(a.arbeidsgiverOrgnr());
        }
        if (a.arbeidsgiverAktorId() != null) {
            return identifikator.equalsIgnoreCase(a.arbeidsgiverAktorId());
        }
        if (a.personArbeidsgiver() != null) {
            return identifikator.equalsIgnoreCase(a.personArbeidsgiver().getAktørIdent());
        }
        return false;
    }
}
