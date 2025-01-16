package no.nav.foreldrepenger.generator.inntektsmelding.erketyper;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.common.domain.ArbeidsgiverIdentifikator;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Orgnummer;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;

public class InntektsmeldingForeldrepengeErketyper {

    private InntektsmeldingForeldrepengeErketyper() {
    }

    public static InntektsmeldingBuilder lagInntektsmelding(Integer beløp, Fødselsnummer fnr, LocalDate fpStartdato, ArbeidsgiverIdentifikator arbeidsgiverIdentifikator) {
        if (arbeidsgiverIdentifikator instanceof Orgnummer o) {
            return InntektsmeldingBuilder.builder()
                    .medBeregnetInntekt(BigDecimal.valueOf(beløp))
                    .medArbeidstakerFnr(fnr.value())
                    .medYtelse(Inntektsmelding.YtelseType.FORELDREPENGER)
                    .medFørsteFraværsdag(fpStartdato)
                    .medAvsendersystem("FS22", "1.0")
                    .medArbeidsgiver(o.value(), "41925090");
        }
        throw new IllegalStateException("Bruk metode lagInntektsmeldingPrivateArbeidsgiver() siden det er privat arbeidsgiver!");
    }

    public static InntektsmeldingBuilder lagInntektsmeldingPrivateArbeidsgiver(Integer beløp, Fødselsnummer fnr,
                                                                               LocalDate fpStartdato,
                                                                               Fødselsnummer fnrArbeidsgiver) {
        return InntektsmeldingBuilder.builder()
                .medBeregnetInntekt(BigDecimal.valueOf(beløp))
                .medArbeidstakerFnr(fnr.value())
                .medYtelse(Inntektsmelding.YtelseType.FORELDREPENGER)
                .medFørsteFraværsdag(fpStartdato)
                .medAvsendersystem("FS22", "1.0")
                .medArbeidsgiverPrivat(fnrArbeidsgiver.value(), "41925090");
    }
}
