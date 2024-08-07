package no.nav.foreldrepenger.autotest.fpkalkulus.foreldrepenger;


import static no.nav.foreldrepenger.generator.kalkulus.ForeslåBeregningTjeneste.fastsettInntektVedAvvik;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getFortsettBeregningListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentDetaljertListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterListeRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.qameta.allure.Description;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.foreldrepenger.autotest.fpkalkulus.Beregner;
import no.nav.foreldrepenger.generator.kalkulus.ForeslåBeregningTjeneste;

@Tag("fpkalkulus")
class BesteberegningTest extends Beregner {

    @DisplayName("Besteberegning - Arbeidstaker med dagpenger i opptjeningsperioden. Beregning etter kap 8 gir bedre resultat.")
    @Description("Besteberegning - Arbeidstaker med dagpenger i opptjeningsperioden. " +
            "Får avvik i foreslå-steget. " +
            "Sjekker for besteberegning, men kap 8 gir bedre beregning.")
    @Test
    void besteberegning_for_arbeidstaker_med_dagpenger_i_opptjeningsperioden_bruker_ikke_seks_beste_måneder(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        var tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        saksbehandler.håndterBeregning(lagHåndterListeRequest(request, fastsettInntektVedAvvik(Map.of(1L, 536800))));

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BESTEBEREGNING);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertListeRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);

        var forventetResultat = hentForventetResultat(testInfo);

        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }

    @DisplayName("Besteberegning - Arbeidstaker med dagpenger i opptjeningsperioden. Seks beste måneder gir best resultat.")
    @Description("Besteberegning - Arbeidstaker med dagpenger i opptjeningsperioden. Bruker seks beste måneder.")
    @Test
    void besteberegning_for_arbeidstaker_med_dagpenger_i_opptjeningsperioden_bruker_seks_beste_måneder(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        var tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        saksbehandler.håndterBeregning(lagHåndterListeRequest(request, fastsettInntektVedAvvik(Map.of(1L, 300000))));

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BESTEBEREGNING);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertListeRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);

        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }

    @DisplayName("Besteberegning - Arbeidstaker med dagpenger på skjæringstidspunktet")
    @Description("Besteberegning - Arbeidstaker med dagpenger på skjæringstidspunktet. " +
            "Avvik på arbeidsinntekt, 3. ledd gir best beregning for søker.")
    @Test
    void besteberegning_med_dagpenger_på_skjæringstidspunktet(TestInfo testInfo) throws Exception {
        var request = opprettTestscenario(testInfo);
        var tilstandResponse = overstyrer.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        saksbehandler.håndterBeregning(
                lagHåndterListeRequest(request, ForeslåBeregningTjeneste.fastsettInntektVedAvvik(Map.of(1L, 12000), null)));

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BESTEBEREGNING);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertListeRequest(request);
        var beregningsgrunnlagGrunnlagDto = overstyrer.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }

}
