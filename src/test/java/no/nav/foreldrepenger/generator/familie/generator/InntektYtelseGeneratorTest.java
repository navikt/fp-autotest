package no.nav.foreldrepenger.generator.familie.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.vtp.kontrakter.person.BrregDto;

@Tag("internal")
class InntektYtelseGeneratorTest {

    @Test
    @DisplayName("Selvstendig næringsdrivende får Sigrun-inntekt og registrert virksomhet fra Brreg")
    void selvstendigNæringsdrivendeOppretterSigrunOgBrreg() {
        var næringsinntekt = 200_000;
        var inneværendeÅr = LocalDate.now().getYear();

        var inntektYtelse = InntektYtelseGenerator.ny()
                .selvstendigNæringsdrivende(næringsinntekt)
                .build();

        assertThat(inntektYtelse.sigrun().inntektår())
                .hasSize(5)
                .allSatisfy(inntektsår -> assertThat(inntektsår.beløp()).isEqualTo(næringsinntekt))
                .extracting(inntektsår -> inntektsår.år())
                .containsExactly(
                        inneværendeÅr - 1,
                        inneværendeÅr - 2,
                        inneværendeÅr - 3,
                        inneværendeÅr - 4,
                        inneværendeÅr - 5);
        assertThat(inntektYtelse.brreg().virksomheter()).containsExactly(
                new BrregDto.VirksomhetDto(
                        "999999999",
                        "VTP FISKE",
                        "ENK",
                        "Enkeltpersonforetak",
                        "03.110",
                        "Hav- og kystfiske"));
    }
}
