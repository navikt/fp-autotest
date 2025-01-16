package no.nav.foreldrepenger.generator.inntektsmelding.erketyper;

import java.math.BigDecimal;

import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;

public class InntektsmeldingSvangerskapspengerErketyper {

    private InntektsmeldingSvangerskapspengerErketyper() {
    }

    public static InntektsmeldingBuilder lagSvangerskapspengerInntektsmelding(Fødselsnummer fnr, Integer beløp,
                                                                              ArbeidsgiverIdentifikator arbeidsgiverIdentifikator) {
        if (arbeidsgiverIdentifikator instanceof Orgnummer o) {
            return lagSvangerskapspengerInntektsmelding(fnr, beløp, o);
        }
        throw new IllegalStateException("Ikke støttet. For privat arbeidgiver bruk lagInntektsmeldingPrivateArbeidsgiver()");

    }

    public static InntektsmeldingBuilder lagSvangerskapspengerInntektsmelding(Fødselsnummer fnr, Integer beløp,
                                                                              Orgnummer orgnummer) {
        return InntektsmeldingBuilder.builder()
                .medArbeidstakerFnr(fnr.value())
                .medBeregnetInntekt(BigDecimal.valueOf(beløp))
                .medYtelse(Inntektsmelding.YtelseType.SVANGERSKAPSPENGER)
                .medArbeidsgiver(orgnummer.value(), "41925090")
                .medAvsendersystem("FS32", "1.0");
    }

    public static InntektsmeldingBuilder lagInntektsmeldingPrivateArbeidsgiver(Fødselsnummer fnr, Integer beløp,
            Fødselsnummer fnrArbeidsgiver) {
        return InntektsmeldingBuilder.builder()
                .medArbeidstakerFnr(fnr.value())
                .medBeregnetInntekt(BigDecimal.valueOf(beløp))
                .medYtelse(Inntektsmelding.YtelseType.SVANGERSKAPSPENGER)
                .medAvsendersystem("FS32", "1.0")
                .medArbeidsgiverPrivat(fnrArbeidsgiver.value(), "41925090");
    }
}
