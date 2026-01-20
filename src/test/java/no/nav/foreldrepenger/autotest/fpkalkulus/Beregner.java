package no.nav.foreldrepenger.autotest.fpkalkulus;

import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmFordelingTjeneste.lagHåndterFordelingRequest;
import static no.nav.foreldrepenger.generator.kalkulus.ForeslåBeregningTjeneste.fastsettInntektVedAvvik;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getFortsettBeregningRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentDetaljertRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentGUIRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.TestInfo;

import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelBeregnRequestDto;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.folketrygdloven.kalkulus.kodeverk.Inntektskategori;
import no.nav.foreldrepenger.kalkulus.kontrakt.response.TilstandResponse;
import no.nav.foreldrepenger.autotest.base.KalkulusTestBase;

public class Beregner extends KalkulusTestBase {

    protected EnkelBeregnRequestDto beregnMedAvvik(TestInfo testInfo, Map<Long, Integer> årsinntektPrAndel, Integer frilansinntekt, boolean skalVerifisere) throws IOException {
        var request = opprettTestscenario(testInfo);
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        saksbehandler.håndterBeregning(lagHåndterRequest(request, fastsettInntektVedAvvik(årsinntektPrAndel, frilansinntekt)));

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(fortsettBeregningRequest, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
        assertThat(tilstandResponse.getVilkårResultat()).isNotNull();
        assertThat(tilstandResponse.getVilkårResultat().getErVilkarOppfylt()).isTrue();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        if (skalVerifisere) {
            var hentRequest = getHentDetaljertRequest(request);
            var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
            var forventetResultat = hentForventetResultat(testInfo);
            assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);;
        }

        return fortsettBeregningRequest;
    }

    protected void behandleUtenAksjonspunkter(TestInfo testInfo) throws IOException {
        behandleUtenAksjonspunkter(testInfo, null, true);
    }

    protected EnkelBeregnRequestDto behandleUtenAksjonspunkter(TestInfo testInfo, String inputPrefix, boolean skalVerifisereSluttresultat) throws IOException {

        var request = inputPrefix != null ? opprettTestscenario(testInfo, inputPrefix) : opprettTestscenario(testInfo);
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIRequest(request));
        var forventetGUIKofakber = hentForventetGUIKofakber(testInfo);
        if (forventetGUIKofakber != null) {
            assertThat(beregningsgrunnlagDto).usingRecursiveComparison().ignoringCollectionOrder().ignoringExpectedNullFields().isEqualTo(forventetGUIKofakber);
        }

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
        assertThat(tilstandResponse.getVilkårResultat()).isNotNull();
        assertThat(tilstandResponse.getVilkårResultat().getErVilkarOppfylt()).isTrue();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        if (skalVerifisereSluttresultat) {
            var hentRequest = getHentDetaljertRequest(request);
            var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
            var forventetResultat = hentForventetResultat(testInfo);
            assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
        }
        return request;
    }


    protected EnkelBeregnRequestDto behandleMedManuellFordeling(TestInfo testInfo,
                                                             String prefix, Map<Long, Integer> beløpMap,
                                                             Map<Long, Inntektskategori> inntektskategoriMap,
                                                             Map<Long, Integer> refusjonsMap, boolean skalVerifisereResultat) throws IOException {

        var request = prefix == null ? opprettTestscenario(testInfo) : opprettTestscenario(testInfo, prefix);
        var tilstandResponse = behandleTilFordel(request, testInfo);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        var fordelBeregningsgrunnlag = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIRequest(request));
        saksbehandler.håndterBeregning(lagHåndterFordelingRequest(
                request,
                fordelBeregningsgrunnlag,
                beløpMap,
                inntektskategoriMap,
                refusjonsMap));


        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);

        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        if (skalVerifisereResultat) {
            var hentRequest = getHentDetaljertRequest(request);
            var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
            var forventetResultat = hentForventetResultat(testInfo);
            assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);;
        }

        return request;
    }

    private TilstandResponse behandleTilFordel(EnkelBeregnRequestDto request, TestInfo testInfo) throws IOException {
        behandleTilVurderVilkår(request, testInfo);
        TilstandResponse tilstandResponse;
        tilstandResponse = behandleFraVurderRefusjonTilFordel(request);
        return tilstandResponse;
    }

    private void behandleTilVurderVilkår(EnkelBeregnRequestDto request, TestInfo testInfo) throws IOException {
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIRequest(request));
        var forventetGUIKofakber = hentForventetGUIKofakber(testInfo);
        if (forventetGUIKofakber != null) {
            assertThat(beregningsgrunnlagDto).isEqualToComparingFieldByField(forventetGUIKofakber);
        }

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(fortsettBeregningRequest, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
        assertThat(tilstandResponse.getVilkårResultat()).isNotNull();
        assertThat(tilstandResponse.getVilkårResultat().getErVilkarOppfylt()).isTrue();
    }

    private TilstandResponse behandleFraVurderRefusjonTilFordel(EnkelBeregnRequestDto request) {
        TilstandResponse tilstandResponse;

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        return tilstandResponse;
    }
}
