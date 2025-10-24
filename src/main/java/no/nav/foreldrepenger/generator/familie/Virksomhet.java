package no.nav.foreldrepenger.generator.familie;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.generator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper;
import no.nav.foreldrepenger.generator.inntektsmelding.erketyper.InntektsmeldingSvangerskapspengerErketyper;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Orgnummer;


public class Virksomhet extends Arbeidsgiver {

    public Virksomhet(Orgnummer orgnummer, Arbeidstaker arbeidstaker, List<Arbeidsforhold> arbeidsforhold, Innsender innsender) {
        super(orgnummer.value(), arbeidstaker, arbeidsforhold, innsender);
    }

    protected InntektsmeldingBuilder lagInntektsmeldingFP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId, LocalDate startdatoForeldrepenger) {
        return InntektsmeldingForeldrepengeErketyper
                .lagInntektsmelding(månedsinntekt, arbeidstaker.fødselsnummer(), startdatoForeldrepenger, new Orgnummer(arbeidsgiverIdentifikator))
                .medArbeidsforholdId(arbeidsforholdId != null ? arbeidsforholdId.value() : null);
    }

    protected InntektsmeldingBuilder lagInntektsmeldingSVP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId) {
        return InntektsmeldingSvangerskapspengerErketyper
                .lagSvangerskapspengerInntektsmelding(arbeidstaker.fødselsnummer(), månedsinntekt, new Orgnummer(arbeidsgiverIdentifikator))
                .medArbeidsforholdId(arbeidsforholdId != null ? arbeidsforholdId.value() : null);
    }

}
