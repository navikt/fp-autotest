package no.nav.foreldrepenger.generator.inntektsmelding;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;

@Tag("internal")
class InntektsmeldingBuilderTest {

    @Test
    void full_im_test() {
        var now = LocalDate.now();
        var expectedFnr = "121313212313";
        var expectedOrgnr = "13213123";
        var expectedTlf = "3333333";
        var expectedInntekt = 30_000;
        var expectedFoersteFravaersdag = now.minusWeeks(3);
        var expectedAvsenderSystem = "testSystem";
        var expectedAvsenderSysteVersjon = "testSystemVersjon";
        var expectedArbeidsforholdId = "arbeidsforholdId";
        var expectedUtsettelseTom = now.plusWeeks(2);
        var expectedRefusjonBeloepProsent = Prosent.valueOf(50);

        var inntektsmeldingBuilder = InntektsmeldingBuilder.builder()
                .medYtelse(Inntektsmelding.YtelseType.FORELDREPENGER)
                .medArbeidsgiver(expectedOrgnr, expectedTlf)
                .medArbeidstakerFnr(expectedFnr)
                .medBeregnetInntekt(expectedInntekt)
                .medFørsteFraværsdag(expectedFoersteFravaersdag) // per arbeidsforhold brukes i SVP
                .medUtsettelse(Inntektsmelding.UtsettelseÅrsak.LOVBESTEMT_FERIE, now, expectedUtsettelseTom) // kun SVP - Altinn
                .medArbeidsforholdId(expectedArbeidsforholdId)
                .medAvsendersystem(expectedAvsenderSystem, expectedAvsenderSysteVersjon)
                .medRefusjonBeløpPerMnd(expectedRefusjonBeloepProsent)
                .medRefusjonsOpphordato(now)
                .medEndringIRefusjonslist(Map.of(now, BigDecimal.TEN, now.plusWeeks(1), BigDecimal.ONE));

        var im = inntektsmeldingBuilder.build();

        assertSoftly(softly -> {
            softly.assertThat(im).isNotNull();
            softly.assertThat(im.arbeidstakerFnr()).isEqualTo(expectedFnr);
            softly.assertThat(im.arbeidsgiver()).isNotNull();
            softly.assertThat(im.arbeidsgiver().arbeidsgiverIdentifikator()).isNotNull().isEqualTo(expectedOrgnr);
            softly.assertThat(im.arbeidsgiver().kontaktnummer()).isNotNull().isEqualTo(expectedTlf);
            softly.assertThat(im.arbeidsgiver().navn()).isNotNull();
            softly.assertThat(im.arbeidsgiver().erPrivatArbeidsgiver()).isFalse();
            softly.assertThat(im.ytelseType()).isEqualTo(Inntektsmelding.YtelseType.FORELDREPENGER);
            softly.assertThat(im.arbeidsforhold().beregnetInntekt().intValue()).isEqualTo(expectedInntekt);
            softly.assertThat(im.arbeidsforhold().førsteFraværsdag()).isEqualTo(expectedFoersteFravaersdag);
            softly.assertThat(im.arbeidsforhold().arbeidsforholdId()).isEqualTo(expectedArbeidsforholdId);
            softly.assertThat(im.arbeidsforhold().utsettelserList()).isNotNull().isNotEmpty().hasSize(1);
            softly.assertThat(im.arbeidsforhold().utsettelserList().getFirst().årsak())
                    .isEqualTo(Inntektsmelding.UtsettelseÅrsak.LOVBESTEMT_FERIE);
            softly.assertThat(im.arbeidsforhold().utsettelserList().getFirst().fom()).isEqualTo(now);
            softly.assertThat(im.arbeidsforhold().utsettelserList().getFirst().tom()).isEqualTo(expectedUtsettelseTom);
            softly.assertThat(im.avsender().system()).isEqualTo(expectedAvsenderSystem);
            softly.assertThat(im.avsender().versjon()).isEqualTo(expectedAvsenderSysteVersjon);
            softly.assertThat(im.refusjon()).isNotNull();
            softly.assertThat(im.refusjon().refusjonBeløpPrMnd())
                    .isEqualTo(BigDecimal.valueOf(expectedRefusjonBeloepProsent.prosent() / 100 * expectedInntekt));
            softly.assertThat(im.refusjon().refusjonOpphørsdato()).isEqualTo(now);
            softly.assertThat(im.refusjon().refusjonEndringList()).isNotNull().isNotEmpty().hasSize(2);
            softly.assertThat(im.refusjon().refusjonEndringList()).allSatisfy(endringRefusjon -> {
                assertThat(endringRefusjon.fom()).isBetween(now, now.plusWeeks(1));
                assertThat(endringRefusjon.beloepPrMnd()).isNotNull().isBetween(BigDecimal.ONE, BigDecimal.TEN);
            });
        });
    }

    @Test
    void minimal_im_test() {
        var now = LocalDate.now();
        var expectedFnr = "121313212313";
        var expectedOrgnr = "13213123";
        var expectedInntekt = 30_000;

        var im = InntektsmeldingBuilder.builder()
                .medYtelse(Inntektsmelding.YtelseType.SVANGERSKAPSPENGER)
                .medArbeidstakerFnr(expectedFnr)
                .medArbeidsgiver(expectedOrgnr, null)
                .medBeregnetInntekt(expectedInntekt)
                .medFørsteFraværsdag(now)
                .build();

        assertSoftly(softly -> {
            softly.assertThat(im.ytelseType()).isEqualTo(Inntektsmelding.YtelseType.SVANGERSKAPSPENGER);
            softly.assertThat(im.arbeidstakerFnr()).isEqualTo(expectedFnr);
            softly.assertThat(im.arbeidsforhold().beregnetInntekt()).isEqualTo(BigDecimal.valueOf(expectedInntekt));
            softly.assertThat(im.arbeidsforhold().førsteFraværsdag()).isEqualTo(now);
            softly.assertThat(im.arbeidsgiver()).isNotNull();
            softly.assertThat(im.arbeidsgiver().arbeidsgiverIdentifikator()).isEqualTo(expectedOrgnr);
            softly.assertThat(im.arbeidsgiver().kontaktnummer()).isNull();
            softly.assertThat(im.arbeidsgiver().navn()).isNotBlank().isEqualTo("Corpolarsen");
            softly.assertThat(im.arbeidsgiver().erPrivatArbeidsgiver()).isFalse();
            softly.assertThat(im.avsender()).isNotNull();
            softly.assertThat(im.avsender().system()).isNull();
            softly.assertThat(im.avsender().versjon()).isNull();
            softly.assertThat(im.refusjon()).isNull();
        });
    }
}
