package no.nav.foreldrepenger.autotest.internal.søknad;

import static no.nav.foreldrepenger.generator.soknad.maler.UttaksperioderMaler.uttaksperiode;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.kontrakter.felles.kodeverk.KontoType;
import no.nav.foreldrepenger.soknad.kontrakt.builder.ForeldrepengerBuilder;
import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.uttaksplan.FellesUttaksplanDto.Rolle;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;
import tools.jackson.databind.json.JsonMapper;

@Tag("internal")
class UttaksperioderMalerTest {

    @Test
    @DisplayName("Ny uttaksperiode bevarer søker, konto og helgejusterte datoer")
    @Description("Sjekker vanlig uttak i piloten, uten annen parts uttak og vedtaksdata.")
    void vanligUttak() {
        var periode = uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE,
                LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 20));

        assertThat(periode.fom()).isEqualTo(LocalDate.of(2026, 9, 7));
        assertThat(periode.tom()).isEqualTo(LocalDate.of(2026, 9, 18));
        assertThat(periode.søker().forelder()).isEqualTo(Rolle.MOR);
        assertThat(periode.søker().kontoType()).isEqualTo(KontoType.MØDREKVOTE);
        assertThat(periode.søker().samtidigUttak()).isNull();
        assertThat(periode.søker().gradering()).isNull();
        assertThat(periode.søker().utsettelseÅrsak()).isNull();
        assertThat(periode.søker().overføringÅrsak()).isNull();
        assertThat(periode.søker().morsAktivitet()).isNull();
        assertThat(periode.søker().flerbarnsdager()).isFalse();
        assertThat(periode.søker().resultat()).isNull();
        assertThat(periode.annenPart()).isNull();
        assertThat(periode.annenPartEøs()).isNull();
    }

    @Test
    @DisplayName("Todagersperiode over helg justeres fra fredag til mandag")
    @Description("Bevarer særregelen fra den gamle malen for en periode fra lørdag til søndag.")
    void todagersperiodeOverHelg() {
        var periode = uttaksperiode(Rolle.MOR, KontoType.MØDREKVOTE,
                LocalDate.of(2026, 9, 5), LocalDate.of(2026, 9, 6));

        assertThat(periode.fom()).isEqualTo(LocalDate.of(2026, 9, 4));
        assertThat(periode.tom()).isEqualTo(LocalDate.of(2026, 9, 7));
    }

    @Test
    @DisplayName("Ny uttaksplan serialiserer perioder og samtidig uttaksprosent")
    @Description("Sjekker rollen far/medmor og 100 % samtidig uttak, uten utfylt gammel periodeliste.")
    void samtidigUttakOgJson() {
        var periode = uttaksperiode(Rolle.FAR_MEDMOR, KontoType.FEDREKVOTE,
                LocalDate.of(2026, 9, 7), LocalDate.of(2026, 9, 18), BigDecimal.valueOf(100));
        var søknad = new ForeldrepengerBuilder().medPerioder(List.of(periode)).build();
        var json = DefaultJsonMapper.toJson(søknad);
        var uttaksplan = JsonMapper.builder().build().readTree(json).path("uttaksplan");

        assertThat(periode.søker().forelder()).isEqualTo(Rolle.FAR_MEDMOR);
        assertThat(periode.søker().samtidigUttak().value()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(uttaksplan.path("perioder").isArray()).isTrue();
        assertThat(uttaksplan.path("perioder").size()).isEqualTo(1);
        assertThat(uttaksplan.path("perioder").get(0).path("søker").path("samtidigUttak").decimalValue())
                .isEqualByComparingTo(BigDecimal.valueOf(100));
        var gamlePerioder = uttaksplan.path("uttaksperioder");
        assertThat(gamlePerioder.isMissingNode() || gamlePerioder.isNull()
                || (gamlePerioder.isArray() && gamlePerioder.size() == 0)).isTrue();
    }
}
