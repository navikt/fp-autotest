package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.erketyper.InntektsmeldingSvangerskapspengerErketyper;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;


public class Virksomhet extends Arbeidsgiver {

    public Virksomhet(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator, Arbeidstaker arbeidstaker, List<Arbeidsforhold> arbeidsforhold, Innsender innsender) {
        super(arbeidsgiverIdentifikator, arbeidstaker, arbeidsforhold, innsender);
    }

    protected InntektsmeldingBuilder lagInntektsmeldingFP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId, LocalDate startdatoForeldrepenger) {
        return InntektsmeldingForeldrepengeErketyper
                .lagInntektsmelding(månedsinntekt, arbeidstaker.fødselsnummer(), startdatoForeldrepenger, identifikator)
                .medArbeidsforholdId(arbeidsforholdId != null ? arbeidsforholdId.value() : null);
    }

    protected InntektsmeldingBuilder lagInntektsmeldingSVP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId) {
        return InntektsmeldingSvangerskapspengerErketyper
                .lagSvangerskapspengerInntektsmelding(arbeidstaker.fødselsnummer(), månedsinntekt, identifikator)
                .medArbeidsforholdId(arbeidsforholdId != null ? arbeidsforholdId.value() : null);
    }

}
