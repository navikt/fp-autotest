package no.nav.foreldrepenger.autotest.fpkalkulus.foreldrepenger;


import static no.nav.folketrygdloven.kalkulus.kodeverk.FaktaOmBeregningTilfelle.VURDER_MILITÆR_SIVILTJENESTE;
import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmBeregningTjeneste.lagFaktaOmBeregningHåndterRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getFortsettBeregningListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentDetaljertListeRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.qameta.allure.Description;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fakta.VurderMilitærDto;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.folketrygdloven.kalkulus.response.v1.TilstandResponse;
import no.nav.foreldrepenger.autotest.fpkalkulus.Beregner;
import no.nav.foreldrepenger.generator.kalkulus.FaktaBeregningLagreDtoBuilder;

@Tag("fpkalkulus")
class MilitærTest extends Beregner {


    @DisplayName("Foreldrepenger - Kun militær i opptjeningen ")
    @Description("Foreldrepenger - Søker har kun militær status i opptjeningen. " +
            "Berengingsgrunnlaget settes likt 3G. 80% dekningsgrad")
    @Test
    void foreldrepenger_militær_settes_til_3g_dekningsgrad_80(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        var faktaBeregningLagreDto = FaktaBeregningLagreDtoBuilder.ny()
                .medFaktaOmBeregningTilfeller(List.of(VURDER_MILITÆR_SIVILTJENESTE))
                .medVurderMilitær(lagMilitærVurdering(true))
                .build();
        var håndterRequest = lagFaktaOmBeregningHåndterRequest(request, faktaBeregningLagreDto);
        saksbehandler.håndterBeregning(håndterRequest);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
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
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - Militær og arbeidstaker i opptjening. ")
    @Description("Foreldrepenger - Militær og arbeidstaker i opptjening. " +
            "Arbeid utgjør mindre enn 3G, så militærandelen fyller på resterende.")
    @Test
    void foreldrepenger_arbeid_under_3g_militær_dekker_rest(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        var faktaBeregningLagreDto = FaktaBeregningLagreDtoBuilder.ny()
                .medFaktaOmBeregningTilfeller(List.of(VURDER_MILITÆR_SIVILTJENESTE))
                .medVurderMilitær(lagMilitærVurdering(true))
                .build();
        var håndterRequest = lagFaktaOmBeregningHåndterRequest(request, faktaBeregningLagreDto);
        saksbehandler.håndterBeregning(håndterRequest);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
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
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - Militær og frilans i opptjening. ")
    @Description("Foreldrepenger - Militær og frilans i opptjening. " +
            "Frilans utgjør mer enn 3G, ingenting tildeles militærandel.")
    @Test
    void foreldrepenger_frilans_over_3g_ingenting_til_militær(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        var faktaBeregningLagreDto = FaktaBeregningLagreDtoBuilder.ny()
                .medFaktaOmBeregningTilfeller(List.of(VURDER_MILITÆR_SIVILTJENESTE))
                .medVurderMilitær(lagMilitærVurdering(true))
                .build();
        var håndterRequest = lagFaktaOmBeregningHåndterRequest(request, faktaBeregningLagreDto);
        saksbehandler.håndterBeregning(håndterRequest);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
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
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - Arbeidstaker, næring og militær i opptjeningen. ")
    @Description("Foreldrepenger - Arbeidstaker, næring og militær i opptjeningen. " +
            "Arbeid og næring utgjør mer enn 3G, derfor 0 til militær.")
    @Test
    void foreldrepenger_arbeid_næring_over_3g_0_til_militær(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        var faktaBeregningLagreDto = FaktaBeregningLagreDtoBuilder.ny()
                .medFaktaOmBeregningTilfeller(List.of(VURDER_MILITÆR_SIVILTJENESTE))
                .medVurderMilitær(lagMilitærVurdering(true))
                .build();
        var håndterRequest = lagFaktaOmBeregningHåndterRequest(request, faktaBeregningLagreDto);
        saksbehandler.håndterBeregning(håndterRequest);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
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
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }

    private VurderMilitærDto lagMilitærVurdering(boolean erMilitær) {
        return new VurderMilitærDto(erMilitær);
    }

}
