package no.nav.foreldrepenger.generator.inntektsmelding;


import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import no.nav.foreldrepenger.generator.inntektsmelding.builders.Prosent;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.generator.inntektsmelding.builders.Inntektsmelding;
import no.nav.foreldrepenger.generator.inntektsmelding.builders.InntektsmeldingBuilder;

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
                .medRefusjonsBelopPerMnd(expectedRefusjonBeloepProsent)
                .medRefusjonsOpphordato(now)
                .medEndringIRefusjonslist(Map.of(now, BigDecimal.TEN, now.plusWeeks(1), BigDecimal.ONE))
                ;

        var im = inntektsmeldingBuilder.build();
        assertThat(im).isNotNull();
        assertThat(im.arbeidstakerFnr()).isEqualTo(expectedFnr);
        assertThat(im.arbeidsgiver()).isNotNull();
        assertThat(im.arbeidsgiver().arbeidsgiverIdentifikator()).isNotNull().isEqualTo(expectedOrgnr);
        assertThat(im.arbeidsgiver().kontaktnummer()).isNotNull().isEqualTo(expectedTlf);
        assertThat(im.arbeidsgiver().navn()).isNotNull();
        assertThat(im.arbeidsgiver().erPrivatArbeidsgiver()).isFalse();
        assertThat(im.ytelseType()).isEqualTo(Inntektsmelding.YtelseType.FORELDREPENGER);
        assertThat(im.arbeidsforhold().beregnetInntekt().intValue()).isEqualTo(expectedInntekt);
        assertThat(im.arbeidsforhold().foersteFravaarsdag()).isEqualTo(expectedFoersteFravaersdag);
        assertThat(im.arbeidsforhold().arbeidsforholdId()).isEqualTo(expectedArbeidsforholdId);
        assertThat(im.arbeidsforhold().utsettelse()).isNotNull().isNotEmpty().hasSize(1);
        assertThat(im.arbeidsforhold().utsettelse().getFirst().aarsak()).isEqualTo(Inntektsmelding.UtsettelseÅrsak.LOVBESTEMT_FERIE);
        assertThat(im.arbeidsforhold().utsettelse().getFirst().fom()).isEqualTo(now);
        assertThat(im.arbeidsforhold().utsettelse().getFirst().tom()).isEqualTo(expectedUtsettelseTom);
        assertThat(im.avsender().system()).isEqualTo(expectedAvsenderSystem);
        assertThat(im.avsender().versjon()).isEqualTo(expectedAvsenderSysteVersjon);
        assertThat(im.refusjon()).isNotNull();
        assertThat(im.refusjon().refusjonBeloepPrMnd()).isEqualTo(BigDecimal.valueOf(expectedRefusjonBeloepProsent.prosent() / 100 * expectedInntekt));
        assertThat(im.refusjon().refusjonOpphoersdato()).isEqualTo(now);
        assertThat(im.refusjon().refusjonEndring()).isNotNull().isNotEmpty().hasSize(2);
        assertThat(im.refusjon().refusjonEndring()).allSatisfy(endringRefusjon -> {
            assertThat(endringRefusjon.fom()).isBetween(now, now.plusWeeks(2));
            assertThat(endringRefusjon.beloepPrMnd()).isNotNull().isBetween(BigDecimal.ONE, BigDecimal.TEN);
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

            assertThat(im.ytelseType()).isEqualTo(Inntektsmelding.YtelseType.SVANGERSKAPSPENGER);
            assertThat(im.arbeidstakerFnr()).isEqualTo(expectedFnr);
            assertThat(im.arbeidsforhold().beregnetInntekt()).isEqualTo(BigDecimal.valueOf(expectedInntekt));
            assertThat(im.arbeidsforhold().foersteFravaarsdag()).isEqualTo(now);
            assertThat(im.arbeidsgiver()).isNotNull();
            assertThat(im.arbeidsgiver().arbeidsgiverIdentifikator()).isEqualTo(expectedOrgnr);
            assertThat(im.arbeidsgiver().kontaktnummer()).isNull();
            assertThat(im.arbeidsgiver().navn()).isNotBlank().isEqualTo("Corpolarsen");
            assertThat(im.arbeidsgiver().erPrivatArbeidsgiver()).isFalse();
            assertThat(im.avsender()).isNotNull();
            assertThat(im.avsender().system()).isNull();
            assertThat(im.avsender().versjon()).isNull();
            assertThat(im.refusjon()).isNull();
    }
}
