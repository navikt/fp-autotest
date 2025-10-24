package no.nav.foreldrepenger.generator.inntektsmelding.erketyper;

import java.math.BigDecimal;

import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Orgnummer;

public class InntektsmeldingSvangerskapspengerErketyper {

    private InntektsmeldingSvangerskapspengerErketyper() {
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
