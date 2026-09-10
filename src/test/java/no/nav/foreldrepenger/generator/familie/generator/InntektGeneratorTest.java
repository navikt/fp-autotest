package no.nav.foreldrepenger.generator.familie.generator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.vtp.kontrakter.person.v2.RegistrertNæringsvirksomhetDto;

@Tag("internal")
class InntektGeneratorTest {

    @Test
    @DisplayName("Selvstendig næringsdrivende får Sigrun-inntekt og registrerte virksomheter fra Brreg")
    void selvstendigNæringsdrivendeOppretterSigrunOgBrreg() {
        var næringsinntekt = 200_000;
        var inneværendeÅr = LocalDate.now().getYear();

        var inntektYtelse = InntektGenerator.ny()
                .selvstendigNæringsdrivende(næringsinntekt)
                .registrertNæring(
                        "974760673",
                        "VTP GÅRDSDRIFT",
                        "ENK",
                        "Enkeltpersonforetak",
                        "01.110",
                        "Dyrking av korn")
                .build();

        assertThat(inntektYtelse.skatteopplysninger())
                .hasSize(5)
                .allSatisfy(inntektsår -> assertThat(inntektsår.beløp()).isEqualTo(næringsinntekt))
                .extracting(inntektsår -> inntektsår.år())
                .containsExactly(
                        inneværendeÅr - 1,
                        inneværendeÅr - 2,
                        inneværendeÅr - 3,
                        inneværendeÅr - 4,
                        inneværendeÅr - 5);
        assertThat(inntektYtelse.registrerteNæringsvirksomheter())
                .extracting(RegistrertNæringsvirksomhetDto::organisasjonsnummer,
                        RegistrertNæringsvirksomhetDto::navn)
                .containsExactly(
                        tuple("999999999", "VTP FISKE"),
                        tuple("974760673", "VTP GÅRDSDRIFT"));
        assertThat(inntektYtelse.registrerteNæringsvirksomheter().getFirst())
                .isEqualTo(new RegistrertNæringsvirksomhetDto(
                        "999999999",
                        "VTP FISKE",
                        "ENK",
                        "Enkeltpersonforetak",
                        "03.110",
                        "Hav- og kystfiske"));
    }

}
