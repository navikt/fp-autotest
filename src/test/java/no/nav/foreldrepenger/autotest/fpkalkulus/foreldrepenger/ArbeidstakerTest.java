package no.nav.foreldrepenger.autotest.fpkalkulus.foreldrepenger;


import static no.nav.folketrygdloven.kalkulus.kodeverk.FaktaOmBeregningTilfelle.VURDER_AT_OG_FL_I_SAMME_ORGANISASJON;
import static no.nav.folketrygdloven.kalkulus.kodeverk.FaktaOmBeregningTilfelle.VURDER_MOTTAR_YTELSE;
import static no.nav.folketrygdloven.kalkulus.kodeverk.FaktaOmBeregningTilfelle.VURDER_REFUSJONSKRAV_SOM_HAR_KOMMET_FOR_SENT;
import static no.nav.foreldrepenger.generator.kalkulus.AvklarAktiviteterTjeneste.lagOverstyrAktiviteterDto;
import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmBeregningTjeneste.lagATFLISammeOrgDto;
import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmBeregningTjeneste.lagFaktaOmBeregningHåndterRequest;
import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmBeregningTjeneste.lagMottarYtelseDto;
import static no.nav.foreldrepenger.generator.kalkulus.FaktaOmBeregningTjeneste.vurderRefusjonskravGyldighet;
import static no.nav.foreldrepenger.generator.kalkulus.ForeslåBeregningTjeneste.fastsettInntektVarigEndring;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getFortsettBeregningListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentDetaljertListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.getHentGUIListeRequest;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterListeRequest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import io.qameta.allure.Description;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.folketrygdloven.kalkulus.response.v1.TilstandResponse;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.RefusjonskravSomKommerForSentDto;
import no.nav.foreldrepenger.autotest.fpkalkulus.Beregner;
import no.nav.foreldrepenger.generator.kalkulus.FaktaBeregningLagreDtoBuilder;
import no.nav.foreldrepenger.generator.kalkulus.ForeslåBeregningTjeneste;
import no.nav.foreldrepenger.generator.kalkulus.VurderRefusjonTjeneste;

@Tag("fpkalkulus")
public class ArbeidstakerTest extends Beregner {

    @DisplayName("Foreldrepenger - arbeidsforhold tilkommer etter skjæringstidspunktet og søker refusjon")
    @Description("Foreldrepenger - arbeidsforhold tilkommer etter skjæringstidspunktet og søker refusjon. " +
            "Arbeidsforholdet som tilkommer tilhører samme virksomhet som det som var aktivt før skjæringstidspunktet.")
    @Test
    public void foreldrepenger_arbeidsforhold_tilkommer_på_skjæringstidspunktet(TestInfo testInfo) throws Exception {
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
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var hentRequest = getHentDetaljertListeRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);

        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - arbeidsforhold tilkommer etter skjæringstidspunktet, refusjon > brutto ved stp")
    @Description("Foreldrepenger - arbeidsforhold tilkommer etter skjæringstidspunktet og søker refusjon. " +
            "Gjør at refusjon overstiger brutto på skjæringstidspunktet og det må fordelels utifra brutto / inntekt fra IM.")
    @Test
    public void foreldrepenger_arbeidsforhold_tilkommer_mer_refusjon_enn_brutto(TestInfo testInfo) throws Exception {
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
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var hentRequest = getHentDetaljertListeRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);

        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - arbeidstaker uten inntektsmelding og frilans i samme organisasjon")
    @Description("Foreldrepenger - arbeidstaker uten inntektsmelding og frilans i samme organisasjon.")
    @Test
    public void foreldrepenger_arbeidstaker_uten_inntektsmelding_frilans_samme_org(TestInfo testInfo) throws Exception {

        var request = opprettTestscenario(testInfo);

        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(1);

        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request));
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

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var hentRequest = getHentDetaljertListeRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - arbeidstaker uten inntektsmelding, sender inntektsmelding for revurdering")
    @Description("Foreldrepenger - arbeidstaker uten inntektsmelding, sender inntektsmelding for revurdering.")
    @Test
    @Disabled // Krever koblingrelasjon
    public void foreldrepenger_arbeidstaker_tilkommet_refusjon(TestInfo testInfo) throws Exception {

        var request1 = opprettTestscenario(testInfo, "original");

        kjørUtenAksjonspunkter(request1);

        var request2 = opprettTestscenario(testInfo, "revurdering", request1);
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request2);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(1);
        var andelDto = VurderRefusjonTjeneste.lagVurderRefusjonAndelDto("973861778",
                "127b7791-8f38-4910-9424-0d764d7b2298", LocalDate.of(2020, 3, 1));
        var håndterRequest = VurderRefusjonTjeneste.lagVurderRefusjonRequest(request2, andelDto);
        saksbehandler.håndterBeregning(håndterRequest);

        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request2));
        var forventetGuiFordel = hentForventetGUIFordel(testInfo);
        assertThat(beregningsgrunnlagDto).usingRecursiveComparison().ignoringCollectionOrder().ignoringExpectedNullFields().isEqualTo(forventetGuiFordel);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var hentRequest = getHentDetaljertListeRequest(request2);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - arbeidstaker med revurdering, økt refusjon i revurdering.")
    @Description("Foreldrepenger - arbeidstaker med revurdering, økt refusjon i revurdering.")
    @Test
    @Disabled // Krever koblingrelasjon
    public void foreldrepenger_arbeidstaker_tilkommet_okt_refusjon(TestInfo testInfo) throws Exception {

        var request1 = opprettTestscenario(testInfo, "original");

        kjørUtenAksjonspunkter(request1);

        var request2 = opprettTestscenario(testInfo, "revurdering", request1);
        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request2);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.KOFAKBER);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(1);
        var andelDto = VurderRefusjonTjeneste.lagVurderRefusjonAndelDto("973861778",
                "127b7791-8f38-4910-9424-0d764d7b2298", LocalDate.of(2020, 3, 1), 15000);
        var håndterRequest = VurderRefusjonTjeneste.lagVurderRefusjonRequest(request2, andelDto);
        saksbehandler.håndterBeregning(håndterRequest);

        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request2));
        var forventetGuiFordel = hentForventetGUIFordel(testInfo);
        assertThat(beregningsgrunnlagDto).isEqualToComparingFieldByField(forventetGuiFordel);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request2, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var hentRequest = getHentDetaljertListeRequest(request2);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }


    @DisplayName("Foreldrepenger - Refusjon for virksomhet med 2 arbeidsforhold som avslutter og tilkommer på hver sin side av skjæringstidspunktet")
    @Description("Foreldrepenger - Refusjon for virksomhet med 2 arbeidsforhold. Det ene arbeidsforholdet avslutter dagen før skjæringstidspunktet " +
            "og det andre starter på skjæringstidspuntet. Søker har også oppgitt frilans, men dette overstyres i avklar aktiviteter.")
    @Test
    public void foreldrepenger_at_sn_refusjon_for_virksomhet_med_2_arbeidsforhold_og_overstyring_av_beregningaktiviteter(TestInfo testInfo) throws Exception {
        var request = opprettTestscenario(testInfo);
        TilstandResponse tilstandResponse = overstyrer.kjørBeregning(request);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);
        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request));

        Map<LocalDate, Boolean> skalBrukes = Map.of(
                LocalDate.of(2015, 8, 1), false,
                LocalDate.of(2019, 1, 1), false);
        var håndterRequest = lagHåndterListeRequest(request, lagOverstyrAktiviteterDto(beregningsgrunnlagDto, skalBrukes));
        overstyrer.håndterBeregning(håndterRequest);

        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = overstyrer.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var hentRequest = getHentDetaljertListeRequest(request);
        var beregningsgrunnlagGrunnlagDto = overstyrer.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }


    @DisplayName("Foreldrepenger - AT/SN med totalt BG over 6G. BG og refusjon fra AG under 6G")
    @Description("Foreldrepenger - AT/SN med totalt BG over 6G. BG og refusjon fra AG under 6G")
    @Test
    public void fp_at_sn_bg_over_6G_bg_fra_arbeid_under_6G_med_full_refusjon(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo); // Shaken, not stirred
    }

    @DisplayName("Foreldrepenger - AT og SN med varig endring og avvik. Automatisk fordeling av beregningsgrunnlag fra AT til SN")
    @Description("Foreldrepenger - AT og SN med varig endring og avvik. Automatisk fordeling av beregningsgrunnlag fra AT til SN")
    @Test
    public void fp_at_sn_med_avvik_beregningsgrunnlag_skal_omfordeles_automatisk_grunnet_refusjon(TestInfo testInfo) throws Exception {

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
        var håndterBeregningDto = fastsettInntektVarigEndring(321_540, true);
        saksbehandler.håndterBeregning(lagHåndterListeRequest(request, håndterBeregningDto));

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var hentRequest = getHentDetaljertListeRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }

    @DisplayName("Foreldrepenger - Arbeistaker med arbeid som slutter dagen før skjæringstidspunktet.")
    @Description("Foreldrepenger -  Arbeistaker med arbeid som slutter dagen før skjæringstidspunktet. " +
            "Arbeidsforhold som avslutter før skjæringstidspunktet tas med i beregning. ")
    @Test
    public void fp_arbeid_avslutter_dagen_før_stp(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo);
    }

    @DisplayName("Foreldrepenger - Arbeidstaker med 2 arbeidsforhold, permisjon fra det ene.")
    @Description("Foreldrepenger -  Arbeidstaker med 2 arbeidsforhold, permisjon fra det ene. " +
            "Permitert arbeidsforhold skal ikke bli med i beregningen. ")
    @Test
    public void fp_arbeidsforhold_med_permisjon(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo);
    }

    @DisplayName("Foreldrepenger - Arbeidstaker med 2 arbeidsforhold i samme bedrift, permisjon fra det ene.")
    @Description("Foreldrepenger -  Arbeidstaker med 2 arbeidsforhold i samme bedrift, permisjon fra det ene. " +
            "Felles inntektsmelding. ")
    @Test
    public void fp_flere_arbeidsforhold_samme_org_en_med_perm(TestInfo testInfo) throws Exception {
        behandleUtenAksjonspunkter(testInfo);
    }

    @DisplayName("Foreldrepenger - Tilkommet arbeid med refusjon samtidig som naturalytelse hos en annen arbeidsgiver")
    @Description("Foreldrepenger - Tilkommet arbeid med refusjonskrav. Borfalt naturalytelse hos arbeid som var aktivt fra start. " +
            "Omfordele hele beregningsgrunnlaget inkludert naturalytelse til tilkommet arbeid.")
    @Test
    public void fp_tilkommet_arbeid_og_fordeling_av_naturalytelse(TestInfo testInfo) throws Exception {

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
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        var hentRequest = getHentDetaljertListeRequest(request);
        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
        var forventetResultat = hentForventetResultat(testInfo);
        assertThat(beregningsgrunnlagGrunnlagDto).usingRecursiveComparison().isEqualTo(forventetResultat);
    }

    private void kjørUtenAksjonspunkter(BeregnRequestDto request) {
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
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);

        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto().size()).isEqualTo(0);
    }
}
