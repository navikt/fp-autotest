package no.nav.foreldrepenger.autotest.fpkalkulus.foreldrepenger;


import static no.nav.folketrygdloven.kalkulus.kodeverk.FaktaOmBeregningTilfelle.VURDER_AT_OG_FL_I_SAMME_ORGANISASJON;
import static no.nav.folketrygdloven.kalkulus.kodeverk.FaktaOmBeregningTilfelle.VURDER_MOTTAR_YTELSE;
import static no.nav.foreldrepenger.generator.kalkulus.AvklarAktiviteterTjeneste.lagOverstyrAktiviteterDto;
import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmBeregningTjeneste.lagATFLISammeOrgDto;
import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmBeregningTjeneste.lagFaktaOmBeregningHåndterRequest;
import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmBeregningTjeneste.lagMottarYtelseDto;
import static no.nav.foreldrepenger.generator.kalkulus.ForeslåBeregningTjeneste.fastsettInntektVarigEndring;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getFortsettBeregningRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentDetaljertRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentGUIRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.qameta.allure.Description;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.folketrygdloven.kalkulus.response.v1.TilstandResponse;
import no.nav.foreldrepenger.autotest.fpkalkulus.Beregner;
import no.nav.foreldrepenger.generator.kalkulus.FaktaBeregningLagreDtoBuilder;
import no.nav.foreldrepenger.generator.kalkulus.VurderRefusjonTjeneste;

@Tag("fpkalkulus")
class ArbeidstakerTest extends Beregner {

    @DisplayName("Foreldrepenger - arbeidsforhold tilkommer etter skjæringstidspunktet og søker refusjon")
    @Description("Foreldrepenger - arbeidsforhold tilkommer etter skjæringstidspunktet og søker refusjon. " +
            "Arbeidsforholdet som tilkommer tilhører samme virksomhet som det som var aktivt før skjæringstidspunktet.")
    @Test
    void foreldrepenger_arbeidsforhold_tilkommer_på_skjæringstidspunktet(TestInfo testInfo) throws Exception {
        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);

        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - arbeidsforhold tilkommer etter skjæringstidspunktet, refusjon > brutto ved stp")
    @Description("Foreldrepenger - arbeidsforhold tilkommer etter skjæringstidspunktet og søker refusjon. " +
            "Gjør at refusjon overstiger brutto på skjæringstidspunktet og det må fordelels utifra brutto / inntekt fra IM.")
    @Test
    void foreldrepenger_arbeidsforhold_tilkommer_mer_refusjon_enn_brutto(TestInfo testInfo) throws Exception {
        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);

        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - arbeidstaker uten inntektsmelding og frilans i samme organisasjon")
    @Description("Foreldrepenger - arbeidstaker uten inntektsmelding og frilans i samme organisasjon.")
    @Test
    void foreldrepenger_arbeidstaker_uten_inntektsmelding_frilans_samme_org(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);

        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIRequest(request));
        var forventetGUIKofakber = hentForventetGUIKofakber(testInfo);

        assertThat(beregningsgrunnlagDto).usingRecursiveComparison().ignoringCollectionOrder().ignoringExpectedNullFields().isEqualTo(forventetGUIKofakber);

        Map<Long, Integer> beløpMap = Map.of(1L, 20000, 2L, 25000);
        var faktaBeregningLagreDto = FaktaBeregningLagreDtoBuilder.ny()
                .medFaktaOmBeregningTilfeller(List.of(VURDER_MOTTAR_YTELSE, VURDER_AT_OG_FL_I_SAMME_ORGANISASJON))
                .medMottarYtelse(lagMottarYtelseDto(Map.of(1L, true, 2L, true), beregningsgrunnlagDto))
                .medVurderATogFLiSammeOrganisasjonDto(lagATFLISammeOrgDto(beløpMap))
                .build();
        var håndterRequest = lagFaktaOmBeregningHåndterRequest(request, faktaBeregningLagreDto);
        saksbehandler.håndterBeregning(håndterRequest);

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - arbeidstaker uten inntektsmelding, sender inntektsmelding for revurdering")
    @Description("Foreldrepenger - arbeidstaker uten inntektsmelding, sender inntektsmelding for revurdering.")
    @Test
    void foreldrepenger_arbeidstaker_tilkommet_refusjon(TestInfo testInfo) throws Exception {

        var request1 = opprettTestscenario(testInfo, "original");

        kjørUtenAksjonspunkter(request1);

        var request2 = opprettTestscenario(testInfo, "revurdering", request1);
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request2);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);
        var andelDto = VurderRefusjonTjeneste.lagVurderRefusjonAndelDto("974652269",
                "127b7791-8f38-4910-9424-0d764d7b2298", LocalDate.of(2020, 3, 1));
        var håndterRequest = VurderRefusjonTjeneste.lagVurderRefusjonRequest(request2, andelDto);
        saksbehandler.håndterBeregning(håndterRequest);

        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIRequest(request2));
        var forventetGuiFordel = hentForventetGUIFordel(testInfo);
        assertThat(beregningsgrunnlagDto).usingRecursiveComparison().ignoringCollectionOrder().ignoringExpectedNullFields().isEqualTo(forventetGuiFordel);

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertRequest(request2);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - arbeidstaker med revurdering, økt refusjon i revurdering.")
    @Description("Foreldrepenger - arbeidstaker med revurdering, økt refusjon i revurdering.")
    @Test
    void foreldrepenger_arbeidstaker_tilkommet_okt_refusjon(TestInfo testInfo) throws Exception {

        var request1 = opprettTestscenario(testInfo, "original");

        kjørUtenAksjonspunkter(request1);

        var request2 = opprettTestscenario(testInfo, "revurdering", request1);
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request2);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);
        var andelDto = VurderRefusjonTjeneste.lagVurderRefusjonAndelDto("974652269",
                "127b7791-8f38-4910-9424-0d764d7b2298", LocalDate.of(2020, 3, 1), 15000);
        var håndterRequest = VurderRefusjonTjeneste.lagVurderRefusjonRequest(request2, andelDto);
        saksbehandler.håndterBeregning(håndterRequest);

        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIRequest(request2));
        var forventetGuiFordel = hentForventetGUIFordel(testInfo);
        assertThat(beregningsgrunnlagDto).usingRecursiveComparison().ignoringCollectionOrder().ignoringExpectedNullFields().isEqualTo(forventetGuiFordel);

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request2, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertRequest(request2);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }


    @DisplayName("Foreldrepenger - Refusjon for virksomhet med 2 arbeidsforhold som avslutter og tilkommer på hver sin side av skjæringstidspunktet")
    @Description("Foreldrepenger - Refusjon for virksomhet med 2 arbeidsforhold. Det ene arbeidsforholdet avslutter dagen før skjæringstidspunktet " +
            "og det andre starter på skjæringstidspuntet. Søker har også oppgitt frilans, men dette overstyres i avklar aktiviteter.")
    @Test
    void foreldrepenger_at_sn_refusjon_for_virksomhet_med_2_arbeidsforhold_og_overstyring_av_beregningaktiviteter(TestInfo testInfo) throws Exception {
        var request = opprettTestscenario(testInfo);
        TilstandResponse tilstandResponse = overstyrer.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIRequest(request));

        Map<LocalDate, Boolean> skalBrukes = Map.of(
                LocalDate.of(2015, 8, 1), false,
                LocalDate.of(2019, 1, 1), false);
        var håndterRequest = lagHåndterRequest(request, lagOverstyrAktiviteterDto(beregningsgrunnlagDto, skalBrukes));
        overstyrer.håndterBeregning(håndterRequest);

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertRequest(request);
        var beregningsgrunnlagGrunnlagDto = overstyrer.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }


    @DisplayName("Foreldrepenger - AT/SN med totalt BG over 6G. BG og refusjon fra AG under 6G")
    @Description("Foreldrepenger - AT/SN med totalt BG over 6G. BG og refusjon fra AG under 6G")
    @Test
    void fp_at_sn_bg_over_6G_bg_fra_arbeid_under_6G_med_full_refusjon(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo); // Shaken, not stirred
    }

    @DisplayName("Foreldrepenger - AT og SN med varig endring og avvik. Automatisk fordeling av beregningsgrunnlag fra AT til SN")
    @Description("Foreldrepenger - AT og SN med varig endring og avvik. Automatisk fordeling av beregningsgrunnlag fra AT til SN")
    @Test
    void fp_at_sn_med_avvik_beregningsgrunnlag_skal_omfordeles_automatisk_grunnet_refusjon(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);
        var håndterBeregningDto = fastsettInntektVarigEndring(321_540, true);
        saksbehandler.håndterBeregning(lagHåndterRequest(request, håndterBeregningDto));

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - Arbeistaker med arbeid som slutter dagen før skjæringstidspunktet.")
    @Description("Foreldrepenger -  Arbeistaker med arbeid som slutter dagen før skjæringstidspunktet. " +
            "Arbeidsforhold som avslutter før skjæringstidspunktet tas med i beregning. ")
    @Test
    void fp_arbeid_avslutter_dagen_før_stp(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo);
    }

    @DisplayName("Foreldrepenger - Arbeidstaker med 2 arbeidsforhold, permisjon fra det ene.")
    @Description("Foreldrepenger -  Arbeidstaker med 2 arbeidsforhold, permisjon fra det ene. " +
            "Permitert arbeidsforhold skal ikke bli med i beregningen. ")
    @Test
    void fp_arbeidsforhold_med_permisjon(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo);
    }

    @DisplayName("Foreldrepenger - Arbeidstaker med 2 arbeidsforhold i samme bedrift, permisjon fra det ene.")
    @Description("Foreldrepenger -  Arbeidstaker med 2 arbeidsforhold i samme bedrift, permisjon fra det ene. " +
            "Felles inntektsmelding. ")
    @Test
    void fp_flere_arbeidsforhold_samme_org_en_med_perm(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo);
    }

    @DisplayName("Foreldrepenger - Tilkommet arbeid med refusjon samtidig som naturalytelse hos en annen arbeidsgiver")
    @Description("Foreldrepenger - Tilkommet arbeid med refusjonskrav. Borfalt naturalytelse hos arbeid som var aktivt fra start. " +
            "Omfordele hele beregningsgrunnlaget inkludert naturalytelse til tilkommet arbeid.")
    @Test
    void fp_tilkommet_arbeid_og_fordeling_av_naturalytelse(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var hentRequest = getHentDetaljertRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(forventetResultat);
    }

    private void kjørUtenAksjonspunkter(BeregnRequestDto request) {
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        var fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();

        fortsettBeregningRequest = getFortsettBeregningRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
    }
}
