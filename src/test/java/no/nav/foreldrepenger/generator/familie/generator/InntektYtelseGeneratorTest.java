package no.nav.foreldrepenger.generator.familie.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

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

    @Test
    @DisplayName("Flere registrerte næringer legges til i stabil rekkefølge")
    void flereRegistrerteNæringer() {
        var inntektYtelse = InntektYtelseGenerator.ny()
                .selvstendigNæringsdrivende(200_000)
                .registrertNæring(
                        "974760673",
                        "VTP GÅRDSDRIFT",
                        "ENK",
                        "Enkeltpersonforetak",
                        "01.110",
                        "Dyrking av korn")
                .build();

        assertThat(inntektYtelse.brreg().virksomheter())
                .extracting(BrregDto.VirksomhetDto::organisasjonsnummer, BrregDto.VirksomhetDto::navn)
                .containsExactly(
                        tuple("999999999", "VTP FISKE"),
                        tuple("974760673", "VTP GÅRDSDRIFT"));
        assertThat(inntektYtelse.sigrun().inntektår())
                .hasSize(5)
                .allSatisfy(inntektsår -> assertThat(inntektsår.beløp()).isEqualTo(200_000));
    }

}
