package no.nav.foreldrepenger.autotest.fpkalkulus.svangerskapspenger;

import org.junit.jupiter.api.Tag;

import no.nav.foreldrepenger.autotest.fpkalkulus.Beregner;


@Tag("fpkalkulus")
class ArbeidstakerOgNæringTest extends Beregner {

// TODO: Tester tilkommet inntekt som vi ikke trenger enda. Se over senere!
//
//    @DisplayName("Svangerskapspenger - Tidsbegrenset arbeidsforhold, sent refusjonskrav, næring tilkommer" +
//            " grunnet søkt ytelse uten næring på stp")
//    @Description("Svangerskapspenger - Tidsbegrenset arbeidsforhold, sent refusjonskrav, næring tilkommer" +
//            " grunnet søkt ytelse uten næring på stp")
//    @Test
//    public void svp_tidsbegrenset_at_tilkommet_sn_grunnet_søkt_ytelse(TestInfo testInfo) throws Exception {
//
//        var request = opprettTestscenario(testInfo);
//
//        TilstandResponse tilstandResponse = saksbehandler.kjørBeregning(request);
//        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
//
//        var fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.KOFAKBER);
//        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
//        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);
//
//        var beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request));
//        Map<String, Boolean> refusjonskravGyldighet = beregningsgrunnlagDto.getFaktaOmBeregning().getRefusjonskravSomKommerForSentListe().stream()
//                .collect(Collectors.toMap(RefusjonskravSomKommerForSentDto::getArbeidsgiverIdent, r -> false));
//        Map<Long, Boolean> erTidsbegrensetMap = beregningsgrunnlagDto.getFaktaOmBeregning().getKortvarigeArbeidsforhold().stream()
//                .collect(Collectors.toMap(FaktaOmBeregningAndelDto::getAndelsnr, k -> true));
//
//        var faktaDto = FaktaBeregningLagreDtoBuilder.ny()
//                .medRefusjonskravGyldighet(vurderRefusjonskravGyldighet(refusjonskravGyldighet))
//                .medVurderTidsbegrensetArbeidsforhold(vurderTidsbegrensetArbeidsforhold(erTidsbegrensetMap))
//                .medFaktaOmBeregningTilfeller(beregningsgrunnlagDto.getFaktaOmBeregning().getFaktaOmBeregningTilfeller())
//                .build();
//
//        saksbehandler.håndterBeregning(lagFaktaOmBeregningHåndterRequest(request, faktaDto));
//
//        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN);
//        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
//        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);
//
//        beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request));
//        var perioder = beregningsgrunnlagDto.getBeregningsgrunnlagPeriode();
//        var periode1 = perioder.get(0);
//        var periode2 = perioder.get(1);
//        Map<Periode, Map<Long, Integer>> periodeInntektMap = Map.of(new Periode(periode1.getBeregningsgrunnlagPeriodeFom(), periode1.getBeregningsgrunnlagPeriodeTom()),
//                Map.of(1L, 248736, 2L, 0),
//                new Periode(periode2.getBeregningsgrunnlagPeriodeFom(), periode2.getBeregningsgrunnlagPeriodeTom()),
//                Map.of(1L, 248736, 2L, 0)
//        );
//        var fastsettTidsbegrenset = ForeslåBeregningTjeneste.fastettBgTidsbegrenset(periodeInntektMap, null);
//        saksbehandler.håndterBeregning(lagHåndterListeRequest(request, fastsettTidsbegrenset));
//
//        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORS_BERGRUNN_2);
//        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
//        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
//
//        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_VILKAR_BERGRUNN);
//        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
//        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
//
//        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_TILKOMMET_INNTEKT);
//        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
//        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);
//
//        beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request));
//        var håndterBeregningListeRequest = FaktaOmFordelingTjeneste.lagHåndterTilkommetInntektsforholdRequest(request, beregningsgrunnlagDto, LocalDateTimeline.empty(), Map.of(
//                InntektsforholdIdentifikator.forArbeidsgiver("974652269"), false,
//                InntektsforholdIdentifikator.forAktivitetStatus(AktivitetStatus.SELVSTENDIG_NÆRINGSDRIVENDE), false));
//
//        saksbehandler.håndterBeregning(håndterBeregningListeRequest);
//
//        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.VURDER_REF_BERGRUNN);
//        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
//        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
//
//        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FORDEL_BERGRUNN);
//        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
//        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).hasSize(1);
//
//        beregningsgrunnlagDto = saksbehandler.hentGUIBeregningsgrunnlag(getHentGUIListeRequest(request));
//        var forventetGUIFordel = hentForventetGUIFordel(testInfo);
//        skrivFaktiskResultatTilFil(testInfo, beregningsgrunnlagDto);
//        assertThat(beregningsgrunnlagDto).isEqualTo(forventetGUIFordel);
//
//        Map<Long, Inntektskategori> inntektskategoriMap = Map.of(1L, Inntektskategori.ARBEIDSTAKER, 2L, Inntektskategori.ARBEIDSTAKER, 3L, Inntektskategori.FRILANSER);
//        Map<Long, Integer> beløpMap = Map.of(1L, 200_000, 2L, 40_000, 3L, 8_736 );
//        var håndterBeregningRequest = lagHåndterFordelingRequest(request, beregningsgrunnlagDto, beløpMap, inntektskategoriMap, Map.of());
//        saksbehandler.håndterBeregning(håndterBeregningRequest);
//
//        fortsettBeregningRequest = getFortsettBeregningListeRequest(request, BeregningSteg.FAST_BERGRUNN);
//        tilstandResponse = saksbehandler.kjørBeregning(fortsettBeregningRequest);
//        assertThat(tilstandResponse.getAvklaringsbehovMedTilstandDto()).isEmpty();
//
//        var hentRequest = getHentDetaljertListeRequest(request);
//        var beregningsgrunnlagGrunnlagDto = saksbehandler.hentDetaljertBeregningsgrunnlag(hentRequest);
//        skrivFaktiskResultatTilFil(testInfo, beregningsgrunnlagGrunnlagDto);
//        var forventetResultat = hentForventetResultat(testInfo);
//        assertThat(beregningsgrunnlagGrunnlagDto).isEqualToComparingFieldByField(forventetResultat);
//    }

}
