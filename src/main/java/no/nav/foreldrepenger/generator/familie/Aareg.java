package no.nav.foreldrepenger.generator.familie;

import static no.nav.foreldrepenger.common.util.StreamUtil.safeStream;

import java.util.List;

import no.nav.foreldrepenger.common.domain.AktørId;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Orgnummer;
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

    static List<Arbeidsforhold> arbeidsforholdene(ArbeidsforholdModell aareg, ArbeidsgiverIdentifikator arbeidsgiverIdentifikator) {
        return safeStream(aareg.arbeidsforhold())
                .filter(a -> erArbeidsgiver(arbeidsgiverIdentifikator, a))
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
                a.arbeidsgiverOrgnr() != null ? new Orgnummer(a.arbeidsgiverOrgnr()) : new AktørId(a.arbeidsgiverAktorId()),
                new ArbeidsforholdId(a.arbeidsforholdId()), a.ansettelsesperiodeFom(), a.ansettelsesperiodeTom(),
                a.arbeidsforholdstype(), a.arbeidsavtaler().getFirst().stillingsprosent());
    }

    private static boolean erArbeidsgiver(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator, no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold a) {
        if (arbeidsgiverIdentifikator == null) {
            return false;
        }
        if (a.arbeidsgiverOrgnr() != null) {
             return arbeidsgiverIdentifikator.value().equalsIgnoreCase(a.arbeidsgiverOrgnr());
        }
        if (a.arbeidsgiverAktorId() != null) {
            return arbeidsgiverIdentifikator.value().equalsIgnoreCase(a.arbeidsgiverAktorId());
        }
        if (a.personArbeidsgiver() != null) {
            return arbeidsgiverIdentifikator.value().equalsIgnoreCase(a.personArbeidsgiver().getAktørIdent());
        }
        return false;
    }
}
