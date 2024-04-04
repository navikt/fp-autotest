package no.nav.foreldrepenger.autotest.fpkalkulus.foreldrepenger;

import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmFordelingTjeneste.lagHåndterFordelingRequest;
import static no.nav.foreldrepenger.generator.kalkulus.ForeslåBeregningTjeneste.fastsettInntektVarigEndring;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getFortsettBeregningListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentDetaljertListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentGUIListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterListeRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.qameta.allure.Description;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.folketrygdloven.kalkulus.kodeverk.Inntektskategori;
import no.nav.folketrygdloven.kalkulus.response.v1.TilstandResponse;
import no.nav.foreldrepenger.autotest.fpkalkulus.Beregner;

@Tag("fpkalkulus")
public class GraderingTest extends Beregner {

    @DisplayName("Foreldrepenger - Søker gradering for arbeid med refusjonskrav.")
    @Description("Foreldrepenger - Søker gradering for arbeid med refusjonskrav.")
    @Test
    public void fp_to_arbeidsforhold_med_gradering_og_refusjon(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo);
    }

    @DisplayName("Foreldrepenger - Søker gradering for arbeid uten refusjonskrav og under 6G total refusjon.")
    @Description("Foreldrepenger - Søker gradering for arbeid uten refusjonskrav og under 6G total refusjon.")
    @Test
    public void fp_to_arbeidsforhold_gradering_uten_refusjon_og_under_6G_refusjon_totalt(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo);
    }

    @DisplayName("Foreldrepenger - Søker gradering for arbeid uten refusjonskrav og over 6G total refusjon.")
    @Description("Foreldrepenger - Søker gradering for arbeid uten refusjonskrav og over 6G total refusjon.")
    @Test
    public void fp_to_arbeidsforhold_gradering_uten_refusjon_og_over_6G_refusjon_totalt(TestInfo testInfo) throws Exception {
        var beløpMap = Map.of(1L, 360_000, 2L, 720_000);
        var refusjonMap = Map.of(2L, 500_000);
        var inntektskategoriMap = Map.of(1L, Inntektskategori.ARBEIDSTAKER, 2L, Inntektskategori.ARBEIDSTAKER);
        behandleMedManuellFordeling(testInfo, null, beløpMap, inntektskategoriMap, refusjonMap, true);
    }

    @DisplayName("Foreldrepenger - Søker gradering for tilkommet arbeid uten refusjonskrav.")
    @Description("Foreldrepenger - Søker gradering for tilkommet arbeid uten refusjonskrav.")
    @Test
    public void fp_arbeid_uten_refusjon_tilkommet_arbeid_med_gradering_uten_refusjon(TestInfo testInfo) throws Exception {
        var beløpMap = Map.of(1L, 360_000, 2L, 360_000);
        Map<Long, Integer> refusjonMap = Map.of();
        var inntektskategoriMap = Map.of(1L, Inntektskategori.ARBEIDSTAKER, 2L, Inntektskategori.ARBEIDSTAKER);
        behandleMedManuellFordeling(testInfo, null, beløpMap, inntektskategoriMap, refusjonMap, true);
    }

    @DisplayName("Foreldrepenger - Søker gradering for tilkommet arbeid med refusjonskrav.")
    @Description("Foreldrepenger - Søker gradering for tilkommet arbeid med refusjonskrav.")
    @Test
    public void fp_arbeid_uten_refusjon_tilkommet_arbeid_med_gradering_med_refusjon(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo);
    }

    @DisplayName("Foreldrepenger - Søker gradering for tilkommet arbeid med refusjonskrav i perioder uten gradering.")
    @Description("Foreldrepenger - Søker gradering for tilkommet arbeid med refusjonskrav i perioder uten gradering.")
    @Test
    public void fp_arbeid_uten_refusjon_tilkommet_arbeid_med_gradering_med_refusjon_i_perioder_uten_gradering(TestInfo testInfo) throws Exception {
        var beløpMap = Map.of(1L, 360_000, 2L, 360_000);
        Map<Long, Integer> refusjonMap = Map.of();
        var inntektskategoriMap = Map.of(1L, Inntektskategori.ARBEIDSTAKER, 2L, Inntektskategori.ARBEIDSTAKER);
        behandleMedManuellFordeling(testInfo, null, beløpMap, inntektskategoriMap, refusjonMap, true);
    }


    @DisplayName("Foreldrepenger - Søker gradering for næring med arbeidsinntekt over 6G.")
    @Description("Foreldrepenger - Søker gradering for næring med arbeidsinntekt over 6G. " +
            "Varig endret næring med avvik og næringsinntekt settest til 0. Aksjonspunkt i fordelsteget grunnet gradering på andel uten beregningsgrunnlag.")
    @Test
    public void foreldrepenger_søker_gradering_for_næring_med_arbeid_over_6G(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(1);

        var håndterBeregningDto = fastsettInntektVarigEndring(0, true);
        saksbehandler.håndterBeregning(lagHåndterListeRequest(request, håndterBeregningDto));

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(1);

        var beregningsgrunnlagDtoFordel = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request));
        var forventetResultatFordel = hentForventetGUIFordel(testInfo);
        assertThat(beregningsgrunnlagDtoFordel).isEqualToComparingFieldByField(forventetResultatFordel);

        var bruttoPrAar = beregningsgrunnlagDtoFordel.getBeregningsgrunnlagPeriode().get(0).getBruttoPrAar();
        Map<Long, Integer> beløpMap = Map.of(1L, bruttoPrAar.verdi().intValue()/2, 2L, bruttoPrAar.verdi().intValue()/2);
        Map<Long, Inntektskategori> inntektskategoriMap = Map.of(
                1L, Inntektskategori.ARBEIDSTAKER,
                2L, Inntektskategori.SELVSTENDIG_NÆRINGSDRIVENDE);
        saksbehandler.håndterBeregning(lagHåndterFordelingRequest(request, beregningsgrunnlagDtoFordel, beløpMap, inntektskategoriMap, Map.of()));

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var hentRequest = getHentDetaljertListeRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).isEqualToComparingFieldByField(forventetResultat);
    }

}
