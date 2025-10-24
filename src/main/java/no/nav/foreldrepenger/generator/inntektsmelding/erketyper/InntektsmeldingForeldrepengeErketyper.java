package no.nav.foreldrepenger.generator.inntektsmelding.erketyper;

import java.math.BigDecimal;
import java.time.LocalDate;

import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Fødselsnummer;
import no.nav.foreldrepenger.kontrakter.fpsoknad.Orgnummer;

public class InntektsmeldingForeldrepengeErketyper {

    private InntektsmeldingForeldrepengeErketyper() {
    }

    public static InntektsmeldingBuilder lagInntektsmelding(Integer beløp, Fødselsnummer fnr, LocalDate fpStartdato, Orgnummer orgnummer) {
            return InntektsmeldingBuilder.builder()
                    .medBeregnetInntekt(BigDecimal.valueOf(beløp))
                    .medArbeidstakerFnr(fnr.value())
                    .medYtelse(Inntektsmelding.YtelseType.FORELDREPENGER)
                    .medFørsteFraværsdag(fpStartdato)
                    .medAvsendersystem("FS22", "1.0")
                    .medArbeidsgiver(orgnummer.value(), "41925090");
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
