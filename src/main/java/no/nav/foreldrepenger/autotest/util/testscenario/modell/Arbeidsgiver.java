package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import static no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;

public abstract class Arbeidsgiver {

    protected final ArbeidsgiverIdentifikator arbeidsgiverIdentifikator;
    protected final Arbeidstaker arbeidstaker;
    protected final List<Arbeidsforhold> arbeidsforhold;
    private final Innsender innsender;

    Arbeidsgiver(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator, Arbeidstaker arbeidstaker,
                               List<Arbeidsforhold> arbeidsforhold, Innsender innsender) {
        this.arbeidsgiverIdentifikator = arbeidsgiverIdentifikator;
        this.arbeidstaker = arbeidstaker;
        this.arbeidsforhold = arbeidsforhold;
        this.innsender = innsender;
    }

    protected abstract InntektsmeldingBuilder lagInntektsmeldingFP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId, LocalDate startdatoForeldrepenger);
    protected abstract InntektsmeldingBuilder lagInntektsmeldingSVP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId);

    public ArbeidsgiverIdentifikator arbeidsgiverIdentifikator() {
        return arbeidsgiverIdentifikator;
    }


    public InntektsmeldingBuilder lagInntektsmeldingFP(LocalDate startdatoForeldrepenger) {
        guardFlereEllerIngenArbeidsforhold(startdatoForeldrepenger);
        return lagInntektsmeldingFP(arbeidstaker.månedsinntekt(), hentArbeidsforholdIdFraAktivtArbeidsforhold(startdatoForeldrepenger),
                startdatoForeldrepenger);
    }

    private ArbeidsforholdId hentArbeidsforholdIdFraAktivtArbeidsforhold(LocalDate startdatoForeldrepenger) {
        return arbeidsforhold.stream()
                .filter(a -> erAktivtArbeidsforhold(a, startdatoForeldrepenger))
                .map(Arbeidsforhold::arbeidsforholdId)
                .findFirst()
                .orElseThrow();
    }

    public List<InntektsmeldingBuilder> lagInntektsmeldingerFP(LocalDate startdatoForeldrepenger) {
        var im = new ArrayList<InntektsmeldingBuilder>();
        if (arbeidsforhold.size() == 1 && erAktivtArbeidsforhold(arbeidsforhold.get(0), startdatoForeldrepenger)) {
            im.add(lagInntektsmeldingFP(startdatoForeldrepenger));
        } else {
            var stillingsprosentSamlet = arbeidsforhold.stream()
                    .filter(a -> erAktivtArbeidsforhold(a, startdatoForeldrepenger))
                    .map(Arbeidsforhold::stillingsprosent)
                    .mapToInt(Integer::intValue).sum();

            arbeidsforhold.stream()
                    .filter(a -> erAktivtArbeidsforhold(a, startdatoForeldrepenger))
                    .forEach(a -> im.add(lagInntektsmeldingFP(
                            arbeidstaker.månedsinntekt() * a.stillingsprosent() / stillingsprosentSamlet,
                            a.arbeidsforholdId(),
                            startdatoForeldrepenger)));
        }
        return im;
    }

    public InntektsmeldingBuilder lagInntektsmeldingTilkommendeArbeidsforholdEtterFPstartdato(LocalDate startdatoForeldrepenger) {
        guardFlereEllerIngenArbeidsforhold(startdatoForeldrepenger);
        return lagInntektsmeldingFP(arbeidstaker.månedsinntekt(), ArbeidsforholdEtterFPstartdato(startdatoForeldrepenger),
                startdatoForeldrepenger);
    }

    public ArbeidsforholdId ArbeidsforholdEtterFPstartdato(LocalDate fpStartdato) {
        return arbeidsforhold.stream()
                .filter(a -> tilkommendeArbeidsforhold(a, fpStartdato))
                .map(Arbeidsforhold::arbeidsforholdId)
                .findFirst()
                .orElseThrow();
    }

    public InntektsmeldingBuilder lagInntektsmeldingSVP() {
        guardFlereEllerIngenArbeidsforhold();
        return lagInntektsmeldingSVP(arbeidstaker.månedsinntekt(), arbeidsforhold.get(0).arbeidsforholdId());
    }

    // TODO: Sender IM for alle arbeidsforhold. Trenger å vite dato for tilrettelegging for å kunne avgjøre
    //  om arbeidsforholdet er aktivt eller ei.
    public List<InntektsmeldingBuilder> lagInntektsmeldingerSVP() {
        var im = new ArrayList<InntektsmeldingBuilder>();
        if(arbeidsforhold.size() == 1) {
            im.add(lagInntektsmeldingSVP());
        } else {
            var stillingsprosentSamlet = arbeidsforhold.stream()
                    .map(Arbeidsforhold::stillingsprosent)
                    .mapToInt(Integer::intValue).sum();

            arbeidsforhold.forEach(a ->
                    im.add(lagInntektsmeldingSVP(arbeidstaker.månedsinntekt() * a.stillingsprosent() / stillingsprosentSamlet,
                            a.arbeidsforholdId())));
        }
        return im;
    }

    public Saksnummer sendInntektsmeldingerSVP(Saksnummer saksnummer) {
        return innsender.sendInnInntektsmelding(lagInntektsmeldingerSVP(), arbeidstaker.aktørId(), arbeidstaker.fødselsnummer(), saksnummer);
    }

    public Saksnummer sendInntektsmeldingerFP(Saksnummer saksnummer, LocalDate startdatoForeldrepenger) {
        return innsender.sendInnInntektsmelding(lagInntektsmeldingerFP(startdatoForeldrepenger), arbeidstaker.aktørId(), arbeidstaker.fødselsnummer(), saksnummer);
    }

    public Saksnummer sendInntektsmeldinger(Saksnummer saksnummer, InntektsmeldingBuilder... inntektsmelding) {
        return innsender.sendInnInntektsmelding(List.of(inntektsmelding), arbeidstaker.aktørId(), arbeidstaker.fødselsnummer(), saksnummer);
    }

    public Saksnummer sendInntektsmeldinger(Saksnummer saksnummer, List<InntektsmeldingBuilder> inntektsmeldinger) {
        return innsender.sendInnInntektsmelding(inntektsmeldinger, arbeidstaker.aktørId(), arbeidstaker.fødselsnummer(), saksnummer);
    }

    protected void guardFlereEllerIngenArbeidsforhold() {
        var antallOrdinæreArbeidsforhold = arbeidsforhold.stream()
                .filter(a -> a.arbeidsforholdstype().equals(ORDINÆRT_ARBEIDSFORHOLD))
                .map(Arbeidsforhold::arbeidsgiverIdentifikasjon)
                .distinct()
                .count();
        if (antallOrdinæreArbeidsforhold > 1) {
            throw new UnsupportedOperationException("Det er flere arbeidsforhold! Bruk metode for å lage flere inntektsmeldiger istedenfor!");
        }
        if (antallOrdinæreArbeidsforhold == 0) {
            throw new IllegalStateException("Kan ikke lage IM når det ikke finnes arbeidsforhold definert på søker");
        }
    }

    protected void guardFlereEllerIngenArbeidsforhold(LocalDate startdatoForeldrepenger) {
        var antallOrdinæreArbeidsforhold = arbeidsforhold.stream()
                .filter(a -> erAktivtArbeidsforhold(a, startdatoForeldrepenger) || tilkommendeArbeidsforhold(a,startdatoForeldrepenger))
                .map(Arbeidsforhold::arbeidsgiverIdentifikasjon)
                .distinct()
                .count();
        if (antallOrdinæreArbeidsforhold > 1) {
            throw new UnsupportedOperationException("Det er flere arbeidsforhold! Bruk metode for å lage flere inntektsmeldiger istedenfor!");
        }
        if (antallOrdinæreArbeidsforhold == 0) {
            throw new IllegalStateException("Kan ikke lage IM når det ikke finnes arbeidsforhold definert på søker");
        }
    }

    private boolean erAktivtArbeidsforhold(Arbeidsforhold a, LocalDate startdatoFP) {
        // Arbeidsforhold er frem i tid og ikke aktivt enda
        if (a.ansettelsesperiodeFom().isEqual(startdatoFP) || a.ansettelsesperiodeFom().isAfter(startdatoFP)) {
            return false;
        }
        // Aktivt og ikke frem i tid
        if (a.ansettelsesperiodeTom() == null) {
            return true;
        }
        return a.ansettelsesperiodeTom().isEqual(startdatoFP) || a.ansettelsesperiodeTom().isAfter(startdatoFP);
    }

    private boolean tilkommendeArbeidsforhold(Arbeidsforhold a, LocalDate fpStartdato) {
        if (a.ansettelsesperiodeTom() != null && a.ansettelsesperiodeTom().isBefore(fpStartdato)) {
            return false;
        }
        return a.ansettelsesperiodeFom().isAfter(fpStartdato);
    }
}
