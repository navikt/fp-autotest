package no.nav.foreldrepenger.autotest.util.testscenario.modell;

import static no.nav.foreldrepenger.generator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmeldingPrivateArbeidsgiver;
import static no.nav.foreldrepenger.generator.inntektsmelding.erketyper.InntektsmeldingSvangerskapspengerErketyper.lagInntektsmeldingPrivateArbeidsgiver;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;

class PersonArbeidsgiver extends Arbeidsgiver {

    private final Fødselsnummer fnrArbeidsgiver;

    public PersonArbeidsgiver(ArbeidsgiverIdentifikator arbeidsgiverIdentifikator, Arbeidstaker arbeidstaker,
                              List<Arbeidsforhold> arbeidsforhold, Innsender innsender, Fødselsnummer fnrArbeidsgiver) {
        super(arbeidsgiverIdentifikator, arbeidstaker, arbeidsforhold, innsender);
        this.fnrArbeidsgiver = fnrArbeidsgiver;
    }

    public Fødselsnummer fnrArbeidsgiver() {
        return fnrArbeidsgiver;
    }

    protected InntektsmeldingBuilder lagInntektsmeldingFP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId, LocalDate startdatoForeldrepenger) {
        return lagInntektsmeldingPrivateArbeidsgiver(månedsinntekt, arbeidstaker.fødselsnummer(), startdatoForeldrepenger, fnrArbeidsgiver);
    }

    protected InntektsmeldingBuilder lagInntektsmeldingSVP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId) {
        return lagInntektsmeldingPrivateArbeidsgiver(arbeidstaker.fødselsnummer(), månedsinntekt, fnrArbeidsgiver);
    }
}
