package no.nav.foreldrepenger.generator.soknad.maler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import no.nav.foreldrepenger.soknad.kontrakt.opptjening.NæringDto;

class OpptjeningMalerTest {

    @Test
    void registrertEgenNæringBrukerNyttFormat() {
        var fom = LocalDate.now().minusYears(5);

        var næring = OpptjeningMaler.registrertEgenNæring(
                "999999999", "VTP FISKE", NæringDto.Virksomhetstype.FISKE, fom, 200_000, false);

        assertThat(næring.fom()).isEqualTo(fom);
        assertThat(næring.tom()).isNull();
        assertThat(næring.næringstype()).isEqualTo(NæringDto.Virksomhetstype.FISKE);
        assertThat(næring.navnPåNæringen()).isEqualTo("VTP FISKE");
        assertThat(næring.organisasjonsnummer().value()).isEqualTo("999999999");
        assertThat(næring.næringsinntekt()).isEqualTo(200_000);
        assertThat(næring.registrertINorge()).isTrue();
        assertThat(næring.registrertILand()).isNull();
        assertThat(næring.harBlittYrkesaktivILøpetAvDeTreSisteFerdigliknedeÅrene()).isFalse();
        assertThat(næring.oppstartsdato()).isNull();
        assertThat(næring.hattVarigEndringAvNæringsinntektSiste4Kalenderår()).isFalse();
    }
}
