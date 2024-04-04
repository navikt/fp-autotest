package no.nav.foreldrepenger.generator.kalkulus;


import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterListeRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fordeling.FaktaOmFordelingHåndteringDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fordeling.FordelBeregningsgrunnlagAndelDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fordeling.FordelBeregningsgrunnlagDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fordeling.FordelBeregningsgrunnlagPeriodeDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fordeling.FordelFastsatteVerdierDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fordeling.FordelRedigerbarAndelDto;
import no.nav.folketrygdloven.kalkulus.kodeverk.Inntektskategori;
import no.nav.folketrygdloven.kalkulus.request.v1.HåndterBeregningListeRequest;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.BeregningsgrunnlagDto;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.BeregningsgrunnlagPrStatusOgAndelDto;

public class FaktaOmFordelingTjeneste {

    private FaktaOmFordelingTjeneste() {
        // Skal ikkje instansieres
    }


    public static HåndterBeregningListeRequest lagHåndterFordelingRequest(BeregnRequestDto request,
                                                                          BeregningsgrunnlagDto beregningsgrunnlagDto,
                                                                          Map<Long, Integer> beløpMap,
                                                                          Map<Long, Inntektskategori> inntektskategoriMap,
                                                                          Map<Long, Integer> refusjonskravMap) {
        return lagHåndterListeRequest(request, lagFordelHåndterDto(beregningsgrunnlagDto, beløpMap, inntektskategoriMap, refusjonskravMap));
    }

    private static FaktaOmFordelingHåndteringDto lagFordelHåndterDto(BeregningsgrunnlagDto beregningsgrunnlagDto, Map<Long, Integer> andelsnrBeløpmap, Map<Long, Inntektskategori> inntektskategoriMap, Map<Long, Integer> refusjonskravMap) {
        var faktaOmFordeling = beregningsgrunnlagDto.getFaktaOmFordeling();
        var perioderTilBehandling = faktaOmFordeling.getFordelBeregningsgrunnlag().getFordelBeregningsgrunnlagPerioder()
                .stream()
                .filter(p -> p.isHarPeriodeAarsakGraderingEllerRefusjon() || p.isSkalKunneEndreRefusjon())
                .toList();

        List<FordelBeregningsgrunnlagPeriodeDto> fordelPerioder = perioderTilBehandling.stream()
                .map(p -> {
                    var matchendeBgPeriode = beregningsgrunnlagDto.getBeregningsgrunnlagPeriode()
                            .stream().filter(bgPeriode -> bgPeriode.getBeregningsgrunnlagPeriodeFom().equals(p.getFom())).findFirst().orElse(null);
                    return new FordelBeregningsgrunnlagPeriodeDto(
                            mapTilFastsatteAndeler(
                                    p.getFordelBeregningsgrunnlagAndeler(),
                                    matchendeBgPeriode.getBeregningsgrunnlagPrStatusOgAndel(),
                                    andelsnrBeløpmap,
                                    inntektskategoriMap,
                                    refusjonskravMap), p.getFom(), p.getTom());
                })
                .toList();

        FordelBeregningsgrunnlagDto fordelBeregningsgrunnlagDto = new FordelBeregningsgrunnlagDto(fordelPerioder);

        return new FaktaOmFordelingHåndteringDto(fordelBeregningsgrunnlagDto);
    }

    private static List<FordelBeregningsgrunnlagAndelDto> mapTilFastsatteAndeler(List<no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.FordelBeregningsgrunnlagAndelDto> fordelBeregningsgrunnlagAndeler,
                                                                                 List<BeregningsgrunnlagPrStatusOgAndelDto> andeler,
                                                                                 Map<Long, Integer> andelsnrBeløpMap, Map<Long, Inntektskategori> inntektskategoriMap, Map<Long, Integer> refusjonskravMap) {
        return fordelBeregningsgrunnlagAndeler.stream()
                .map(a -> {
                    BeregningsgrunnlagPrStatusOgAndelDto matchendeBgAndel = andeler.stream().filter(bgAndel -> bgAndel.getAndelsnr().equals(a.getAndelsnr())).findFirst().orElse(null);
                    return new FordelBeregningsgrunnlagAndelDto(
                            lagRedigerbarAndel(a, matchendeBgAndel),
                            new FordelFastsatteVerdierDto(refusjonskravMap.getOrDefault(a.getAndelsnr(), null), andelsnrBeløpMap.get(a.getAndelsnr()), inntektskategoriMap.get(a.getAndelsnr()), andelsnrBeløpMap.get(a.getAndelsnr())),
                            matchendeBgAndel == null || Inntektskategori.UDEFINERT.equals(matchendeBgAndel.getInntektskategori()) ? null : matchendeBgAndel.getInntektskategori(),
                            matchendeBgAndel.getArbeidsforhold() == null ? null : getIntValueOrNull(matchendeBgAndel.getArbeidsforhold().getRefusjonPrAar().verdi()),
                            getIntValueOrNull(matchendeBgAndel.getBruttoPrAar().verdi()));
                }).collect(Collectors.toList());
    }

    private static Integer getIntValueOrNull(BigDecimal value) {
        return value == null ? null : value.intValue();
    }

    private static FordelRedigerbarAndelDto lagRedigerbarAndel(no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.FordelBeregningsgrunnlagAndelDto a, BeregningsgrunnlagPrStatusOgAndelDto bgAndel) {
        return new FordelRedigerbarAndelDto(
                a.getAndelsnr(),
                a.getArbeidsforhold() == null ? null : a.getArbeidsforhold().getArbeidsgiverIdent(),
                a.getArbeidsforhold() == null ? null : a.getArbeidsforhold().getArbeidsforholdId(),
                false,
                a.getKilde());
    }


}
