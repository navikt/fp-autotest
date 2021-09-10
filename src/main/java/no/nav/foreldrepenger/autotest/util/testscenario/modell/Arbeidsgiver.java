package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.modell.felles.Orgnummer;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingSvangerskapspengerErketyper;


public class Arbeidsgiver {

    private final Orgnummer orgnummer;
    private final Arbeidstaker arbeidstaker;
    private final List<Arbeidsforhold> arbeidsforhold;
    private List<InntektsmeldingBuilder> inntektsmeldinger;

    public Arbeidsgiver(Orgnummer orgnummer, Arbeidstaker arbeidstaker, List<Arbeidsforhold> arbeidsforhold) {
        this.orgnummer = orgnummer;
        this.arbeidstaker = arbeidstaker;
        this.arbeidsforhold = arbeidsforhold;
    }

    public Orgnummer orgnummer() {
        return orgnummer;
    }

    public List<InntektsmeldingBuilder> getInntektsmeldinger() {
        return inntektsmeldinger;
    }

    public InntektsmeldingBuilder lagInntektsmeldingFP(LocalDate startdatoForeldrepenger) {
        guardFlereArbeidsforhold();
        var inntektsmeldingBuilder = InntektsmeldingForeldrepengeErketyper.lagInntektsmelding(arbeidstaker.månedsinntekt(),
                arbeidstaker.fødselsnummer(), startdatoForeldrepenger, orgnummer)
                .medArbeidsforholdId(arbeidsforhold.get(0).arbeidsforholdId().arbeidsforholdId());
        this.inntektsmeldinger = List.of(inntektsmeldingBuilder);
        return inntektsmeldingBuilder;
    }

    public List<InntektsmeldingBuilder> lagInntektsmeldingerFP(LocalDate startdatoForeldrepenger) {
        var im = new ArrayList<InntektsmeldingBuilder>();
        if(arbeidsforhold.size() == 1) {
            im.add(lagInntektsmeldingFP(startdatoForeldrepenger));
        } else {
            var stillingsprosentSamlet = arbeidsforhold.stream()
                    .map(Arbeidsforhold::stillingsprosent)
                    .mapToInt(Integer::intValue).sum();

            for (Arbeidsforhold af : arbeidsforhold) {
                im.add(InntektsmeldingForeldrepengeErketyper
                        .lagInntektsmelding(arbeidstaker.månedsinntekt() * af.stillingsprosent() / stillingsprosentSamlet,
                                arbeidstaker.fødselsnummer(), startdatoForeldrepenger, orgnummer)
                        .medArbeidsforholdId(af.arbeidsforholdId().arbeidsforholdId()));
            }
        }
        this.inntektsmeldinger = im;
        return im;
    }

    public InntektsmeldingBuilder lagInntektsmeldingSVP() {
        guardFlereArbeidsforhold();
        var inntektsmeldingBuilder = InntektsmeldingSvangerskapspengerErketyper
                .lagSvangerskapspengerInntektsmelding(arbeidstaker.fødselsnummer(), arbeidstaker.månedsinntekt(), orgnummer)
                .medArbeidsforholdId(arbeidsforhold.get(0).arbeidsforholdId().arbeidsforholdId());
        this.inntektsmeldinger = List.of(inntektsmeldingBuilder);
        return inntektsmeldingBuilder;
    }

    public List<InntektsmeldingBuilder> lagInntektsmeldingerSVP() {
        var im = new ArrayList<InntektsmeldingBuilder>();
        if(arbeidsforhold.size() == 1) {
            im.add(lagInntektsmeldingSVP());
        } else {
            var stillingsprosentSamlet = arbeidsforhold.stream()
                    .map(Arbeidsforhold::stillingsprosent)
                    .mapToInt(Integer::intValue).sum();

            for (Arbeidsforhold af : arbeidsforhold) {
                im.add(InntektsmeldingSvangerskapspengerErketyper
                        .lagSvangerskapspengerInntektsmelding(arbeidstaker.fødselsnummer(),
                                arbeidstaker.månedsinntekt() * af.stillingsprosent() / stillingsprosentSamlet,
                                orgnummer)
                        .medArbeidsforholdId(af.arbeidsforholdId().arbeidsforholdId()));
            }
        }
        this.inntektsmeldinger = im;
        return im;
    }

    public void sendDefaultInntektsmeldingerSVP(long saksnummer) {
        new Innsender(Aktoer.Rolle.SAKSBEHANDLER).sendInnInnteksmeldingFpfordel(lagInntektsmeldingerSVP(), arbeidstaker.fødselsnummer(), saksnummer);
    }

    public void sendDefaultInntektsmeldingerFP(long saksnummer, LocalDate startdatoForeldrepenger) {
        new Innsender(Aktoer.Rolle.SAKSBEHANDLER).sendInnInnteksmeldingFpfordel(lagInntektsmeldingerFP(startdatoForeldrepenger), arbeidstaker.fødselsnummer(), saksnummer);
    }

    public void sendInntektsmeldinger(long saksnummer, InntektsmeldingBuilder... inntektsmelding) {
        new Innsender(Aktoer.Rolle.SAKSBEHANDLER).sendInnInnteksmeldingFpfordel(arbeidstaker.fødselsnummer(), saksnummer, inntektsmelding);
    }

    public void sendInntektsmeldinger(long saksnummer, List<InntektsmeldingBuilder> inntektsmeldinger) {
        new Innsender(Aktoer.Rolle.SAKSBEHANDLER).sendInnInnteksmeldingFpfordel(inntektsmeldinger, arbeidstaker.fødselsnummer(), saksnummer);
    }

    private void guardFlereArbeidsforhold() {
        if (arbeidsforhold.size() > 1) {
            throw new UnsupportedOperationException("Det er flere arbeidsforhold...");
        }
    }
}
