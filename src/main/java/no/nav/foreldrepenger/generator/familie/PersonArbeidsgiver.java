package no.nav.foreldrepenger.generator.familie;

import static no.nav.foreldrepenger.generator.inntektsmelding.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmeldingPrivateArbeidsgiver;
import static no.nav.foreldrepenger.generator.inntektsmelding.erketyper.InntektsmeldingSvangerskapspengerErketyper.lagInntektsmeldingPrivateArbeidsgiver;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.aktoerer.innsender.Innsender;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;

class PersonArbeidsgiver extends Arbeidsgiver {

    private final Ident identArbeidsgiver;

    public PersonArbeidsgiver(Ident identArbeidsgiver, Arbeidstaker arbeidstaker,
                              List<Arbeidsforhold> arbeidsforhold, Innsender innsender) {
        super(identArbeidsgiver.aktørId().value(), arbeidstaker, arbeidsforhold, innsender);
        this.identArbeidsgiver = identArbeidsgiver;
    }

    public Ident identArbeidsgiver() {
        return identArbeidsgiver;
    }

    protected InntektsmeldingBuilder lagInntektsmeldingFP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId, LocalDate startdatoForeldrepenger) {
        return lagInntektsmeldingPrivateArbeidsgiver(månedsinntekt, arbeidstaker.ident().fødselsnummer(), startdatoForeldrepenger, identArbeidsgiver.fødselsnummer());
    }

    protected InntektsmeldingBuilder lagInntektsmeldingSVP(Integer månedsinntekt, ArbeidsforholdId arbeidsforholdId) {
        return lagInntektsmeldingPrivateArbeidsgiver(arbeidstaker.ident().fødselsnummer(), månedsinntekt, identArbeidsgiver.fødselsnummer());
    }
}
