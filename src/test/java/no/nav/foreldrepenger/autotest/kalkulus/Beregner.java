package no.nav.foreldrepenger.autotest.kalkulus;

import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmFordelingTjeneste.lagHåndterFordelingRequest;
import static no.nav.foreldrepenger.generator.kalkulus.ForeslåBeregningTjeneste.fastsettInntektVedAvvik;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getFortsettBeregningListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentDetaljertListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentGUIListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterListeRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.TestInfo;

import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.folketrygdloven.kalkulus.kodeverk.Inntektskategori;
import no.nav.folketrygdloven.kalkulus.response.v1.TilstandResponse;
import no.nav.foreldrepenger.autotest.base.KalkulusTestBase;

public class Beregner extends KalkulusTestBase {

    protected BeregnRequestDto beregnMedAvvik(TestInfo testInfo, Map<Long, Integer> årsinntektPrAndel, Integer frilansinntekt, boolean skalVerifisere) throws IOException {
        var request = opprettTestscenario(testInfo);
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(1);

        saksbehandler.håndterBeregning(lagHåndterListeRequest(request, fastsettInntektVedAvvik(årsinntektPrAndel, frilansinntekt)));

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        if (skalVerifisere) {
            var hentRequest = getHentDetaljertListeRequest(request);
            var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
            var forventetResultat = hentForventetResultat(testInfo);
            assertThat(beregningsgrunnlagGrunnlagDto).isEqualTo(forventetResultat);
        }

        return fortsettBeregningRequest;
    }

    protected void behandleUtenAksjonspunkter(TestInfo testInfo) throws IOException {
        behandleUtenAksjonspunkter(testInfo, null, true);
    }

    protected BeregnRequestDto behandleUtenAksjonspunkter(TestInfo testInfo, String inputPrefix, boolean skalVerifisereSluttresultat) throws IOException {

        var request = inputPrefix != null ? opprettTestscenario(testInfo, inputPrefix) : opprettTestscenario(testInfo);
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();
        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request));
        var forventetGUIKofakber = hentForventetGUIKofakber(testInfo);
        if (forventetGUIKofakber != null) {
            assertThat(beregningsgrunnlagDto).isEqualToComparingFieldByField(forventetGUIKofakber);
        }

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        if (skalVerifisereSluttresultat) {
            var hentRequest = getHentDetaljertListeRequest(request);
            var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
            var forventetResultat = hentForventetResultat(testInfo);
            var s = beregningsgrunnlagGrunnlagDto.toString();
            var s1 = forventetResultat.toString();
            assertThat(beregningsgrunnlagGrunnlagDto).isEqualTo(forventetResultat);
        }
        return request;
    }


    protected BeregnRequestDto behandleMedManuellFordeling(TestInfo testInfo,
                                                             String prefix, Map<Long, Integer> beløpMap,
                                                             Map<Long, Inntektskategori> inntektskategoriMap,
                                                             Map<Long, Integer> refusjonsMap, boolean skalVerifisereResultat) throws IOException {

        var request = prefix == null ? opprettTestscenario(testInfo) : opprettTestscenario(testInfo, prefix);
        var tilstandResponse = behandleTilFordel(request, testInfo);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(1);

        var fordelBeregningsgrunnlag = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request));
        saksbehandler.håndterBeregning(lagHåndterFordelingRequest(
                request,
                fordelBeregningsgrunnlag,
                beløpMap,
                inntektskategoriMap,
                refusjonsMap));


        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);

        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        if (skalVerifisereResultat) {
            var hentRequest = getHentDetaljertListeRequest(request);
            var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
            var forventetResultat = hentForventetResultat(testInfo);
            assertThat(beregningsgrunnlagGrunnlagDto).isEqualTo(forventetResultat);
        }

        return request;
    }

    private TilstandResponse behandleTilFordel(BeregnRequestDto request, TestInfo testInfo) throws IOException {
        behandleTilVurderVilkår(request, testInfo);
        TilstandResponse tilstandResponse;

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_TILKOMMET_INNTEKT);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        tilstandResponse = behandleFraVurderRefusjonTilFordel(request);
        return tilstandResponse;
    }

    private void behandleTilVurderVilkår(BeregnRequestDto request, TestInfo testInfo) throws IOException {
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request));
        var forventetGUIKofakber = hentForventetGUIKofakber(testInfo);
        if (forventetGUIKofakber != null) {
            assertThat(beregningsgrunnlagDto).isEqualToComparingFieldByField(forventetGUIKofakber);
        }

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();
    }

    private TilstandResponse behandleFraVurderRefusjonTilFordel(BeregnRequestDto request) {
        TilstandResponse tilstandResponse;

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isZero();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        return tilstandResponse;
    }
}
