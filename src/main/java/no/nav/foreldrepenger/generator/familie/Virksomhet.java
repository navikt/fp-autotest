package no.nav.foreldrepenger.generator.familie;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.generator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper;
import no.nav.foreldrepenger.generator.inntektsmelding.erketyper.InntektsmeldingSvangerskapspengerErketyper;


public class Virksomhet extends Arbeidsgiver {

    public Virksomhet(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator, Arbeidstaker arbeidstaker, List<Arbeidsforhold> arbeidsforhold, Innsender innsender) {
        super(arbeidsgiverIdentifikator, arbeidstaker, arbeidsforhold, innsender);
    }

    protected InntektsmeldingBuilder lagInntektsmeldingFP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId, LocalDate startdatoForeldrepenger) {
        return InntektsmeldingForeldrepengeErketyper
                .lagInntektsmelding(månedsinntekt, arbeidstaker.fødselsnummer(), startdatoForeldrepenger, arbeidsgiverIdentifikator)
                .medArbeidsforholdId(arbeidsforholdId != null ? arbeidsforholdId.value() : null);
    }

    protected InntektsmeldingBuilder lagInntektsmeldingSVP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId) {
        return InntektsmeldingSvangerskapspengerErketyper
                .lagSvangerskapspengerInntektsmelding(arbeidstaker.fødselsnummer(), månedsinntekt, arbeidsgiverIdentifikator)
                .medArbeidsforholdId(arbeidsforholdId != null ? arbeidsforholdId.value() : null);
    }

}
