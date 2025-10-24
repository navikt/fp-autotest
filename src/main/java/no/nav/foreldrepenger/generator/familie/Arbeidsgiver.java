package no.nav.foreldrepenger.generator.familie;

import static no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforholdstype.ORDINÆRT_ARBEIDSFORHOLD;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Saksnummer;

public abstract class Arbeidsgiver {
    private static final Logger LOG = LoggerFactory.getLogger(Arbeidsgiver.class);

    protected final String arbeidsgiverIdentifikator;
    protected final Arbeidstaker arbeidstaker;
    protected final List<Arbeidsforhold> arbeidsforhold;
    private final Innsender innsender;

    Arbeidsgiver(String arbeidsgiverIdentifikator, Arbeidstaker arbeidstaker, List<Arbeidsforhold> arbeidsforhold, Innsender innsender) {
        this.arbeidsgiverIdentifikator = arbeidsgiverIdentifikator;
        this.arbeidstaker = arbeidstaker;
        this.arbeidsforhold = arbeidsforhold;
        this.innsender = innsender;
    }

    protected abstract InntektsmeldingBuilder lagInntektsmeldingFP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId, LocalDate startdatoForeldrepenger);
    protected abstract InntektsmeldingBuilder lagInntektsmeldingSVP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId);

    public String arbeidsgiverIdentifikator() {
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
        return lagInntektsmeldingerFP(startdatoForeldrepenger, false);
    }

    public List<InntektsmeldingBuilder> lagInntektsmeldingerFP(LocalDate startdatoForeldrepenger, boolean slåSammenFlereArbeidsforhold) {
        if (arbeidsforhold.isEmpty() || arbeidsforhold.stream().noneMatch(a -> erAktivtArbeidsforhold(a, startdatoForeldrepenger))) {
            LOG.info("Arbeidsgiver {} har ingen aktive arbeidsforhold for dato {}. Sender ingen IM.", arbeidsgiverIdentifikator, startdatoForeldrepenger);
            return List.of();
        }

        if (arbeidsforhold.size() == 1) {
            return List.of(lagInntektsmeldingFP(startdatoForeldrepenger));
        }

        var stillingsprosentSamlet = arbeidsforhold.stream()
                .filter(a -> erAktivtArbeidsforhold(a, startdatoForeldrepenger))
                .map(Arbeidsforhold::stillingsprosent)
                .mapToInt(Integer::intValue)
                .sum();

        if (slåSammenFlereArbeidsforhold) {
            var aktiveInntekter = arbeidsforhold.stream()
                    .filter(a -> erAktivtArbeidsforhold(a, startdatoForeldrepenger))
                    .map(a -> arbeidstaker.månedsinntekt() * a.stillingsprosent() / stillingsprosentSamlet)
                    .mapToInt(Integer::intValue)
                    .sum();
            return List.of(lagInntektsmeldingFP(aktiveInntekter, null, startdatoForeldrepenger));
        }

        return arbeidsforhold.stream()
                .filter(a -> erAktivtArbeidsforhold(a, startdatoForeldrepenger))
                .map(a -> lagInntektsmeldingFP(
                        arbeidstaker.månedsinntekt() * a.stillingsprosent() / stillingsprosentSamlet,
                        a.arbeidsforholdId(),
                        startdatoForeldrepenger))
                .toList();
    }

    public InntektsmeldingBuilder lagInntektsmeldingTilkommendeArbeidsforholdEtterFPstartdato(LocalDate startdatoForeldrepenger) {
        guardFlereEllerIngenArbeidsforhold(startdatoForeldrepenger);
        return lagInntektsmeldingFP(arbeidstaker.månedsinntekt(), arbeidsforholdEtterFPstartdato(startdatoForeldrepenger),
                startdatoForeldrepenger);
    }

    public ArbeidsforholdId arbeidsforholdEtterFPstartdato(LocalDate fpStartdato) {
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
        var im = lagInntektsmeldingerSVP();
        if (im.isEmpty()) {
            LOG.info("Ingen inntektsmeldinger å sende for arbeidsgiver {}", arbeidsgiverIdentifikator);
            return saksnummer;
        }
        return innsender.sendInnInntektsmelding(buildInntektsmeldinger(im), arbeidstaker.aktørId(), arbeidstaker.fødselsnummer(), saksnummer);
    }

    public Saksnummer sendInntektsmeldingerFP(Saksnummer saksnummer, LocalDate startdatoForeldrepenger) {
        var im = lagInntektsmeldingerFP(startdatoForeldrepenger);
        if (im.isEmpty()) {
            LOG.info("Ingen inntektsmeldinger å sende for arbeidsgiver {} for FP-startdato {}.", arbeidsgiverIdentifikator, startdatoForeldrepenger);
            return saksnummer;
        }
        return innsender.sendInnInntektsmelding(buildInntektsmeldinger(im), arbeidstaker.aktørId(), arbeidstaker.fødselsnummer(), saksnummer);
    }

    public Saksnummer sendInnInntektsmeldingUtenForespørsel(Saksnummer saksnummer, InntektsmeldingBuilder im, LocalDate startdato, boolean registrertIAareg) {
        return innsender.sendInnInntektsmeldingUtenForespørsel(im.build(), startdato, arbeidstaker.aktørId(), arbeidstaker.fødselsnummer(), saksnummer, registrertIAareg);
    }

    public Saksnummer sendInntektsmelding(Saksnummer saksnummer, InntektsmeldingBuilder inntektsmelding) {
        return innsender.sendInnInntektsmelding(inntektsmelding.build(), arbeidstaker.aktørId(), arbeidstaker.fødselsnummer(), saksnummer);
    }

    private List<Inntektsmelding> buildInntektsmeldinger(List<InntektsmeldingBuilder> inntektsmeldingStream) {
        return inntektsmeldingStream.stream().map(InntektsmeldingBuilder::build).toList();
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
