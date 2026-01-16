package no.nav.foreldrepenger.generator.kalkulus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import no.nav.folketrygdloven.kalkulus.felles.v1.Periode;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fakta.FastsatteAndelerTidsbegrensetDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fakta.FastsattePerioderTidsbegrensetDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.foreslå.FastsettBGTidsbegrensetArbeidsforholdDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.foreslå.FastsettBGTidsbegrensetArbeidsforholdHåndteringDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.foreslå.FastsettBeregningsgrunnlagATFLHåndteringDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.foreslå.InntektPrAndelDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.foreslå.VurderVarigEndretArbeidssituasjonHåndteringDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.foreslå.VurderVarigEndringEllerNyoppstartetDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.foreslå.VurderVarigEndringEllerNyoppstartetSNHåndteringDto;

public class ForeslåBeregningTjeneste {

    private ForeslåBeregningTjeneste() {
        // Skjul
    }


    public static FastsettBGTidsbegrensetArbeidsforholdHåndteringDto fastettBgTidsbegrenset(
            Map<Periode, Map<Long, Integer>> periodeInntekterMap,
            @Valid @Min(0L) @Max(9223372036854775807L) Integer frilansInntekt) {
        List<FastsattePerioderTidsbegrensetDto> fastsatteTidsbegrensetPerioder = periodeInntekterMap.entrySet().stream().map(periodeEntry -> {
            var fastsatteAndeler = periodeEntry.getValue().entrySet()
                    .stream()
                    .map(e -> new FastsatteAndelerTidsbegrensetDto(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
            return new FastsattePerioderTidsbegrensetDto(periodeEntry.getKey().getFom(), periodeEntry.getKey().getTom(), fastsatteAndeler);
        }).collect(Collectors.toList());
        return new FastsettBGTidsbegrensetArbeidsforholdHåndteringDto(
                new FastsettBGTidsbegrensetArbeidsforholdDto(fastsatteTidsbegrensetPerioder, frilansInntekt)
        );
    }

    public static FastsettBeregningsgrunnlagATFLHåndteringDto fastsettInntektVedAvvik(Map<Long, Integer> årsinntektPrArbeidstakerAndel) {
        var inntektPrAndelList = årsinntektPrArbeidstakerAndel.entrySet().stream()
                .map(e -> new InntektPrAndelDto(e.getValue(), e.getKey()))
                .collect(Collectors.toList());
        return new FastsettBeregningsgrunnlagATFLHåndteringDto(inntektPrAndelList, null);
    }

    public static FastsettBeregningsgrunnlagATFLHåndteringDto fastsettInntektVedAvvik(Map<Long, Integer> årsinntektPrArbeidstakerAndel, Integer frilansinnekt) {
        var inntektPrAndelList = årsinntektPrArbeidstakerAndel.entrySet().stream()
                .map(e -> new InntektPrAndelDto(e.getValue(), e.getKey()))
                .collect(Collectors.toList());
        return new FastsettBeregningsgrunnlagATFLHåndteringDto(inntektPrAndelList, frilansinnekt);
    }

    public static FastsettBeregningsgrunnlagATFLHåndteringDto fastsettInntektVedAvvik(Integer frilansinntekt) {
        return new FastsettBeregningsgrunnlagATFLHåndteringDto(Collections.emptyList(), frilansinntekt);
    }

    public static VurderVarigEndringEllerNyoppstartetSNHåndteringDto fastsettInntektVarigEndring(Integer beløpPrÅr, boolean erVarigEndring) {
        var varigEndringDto = new VurderVarigEndringEllerNyoppstartetDto(erVarigEndring, beløpPrÅr);
        return new VurderVarigEndringEllerNyoppstartetSNHåndteringDto(varigEndringDto);
    }

    public static VurderVarigEndretArbeidssituasjonHåndteringDto fastsettInntektVarigEndretArbeidssitausjon(Integer beløpPrÅr, boolean erVarigEndring) {
        var varigEndringDto = new VurderVarigEndringEllerNyoppstartetDto(erVarigEndring, beløpPrÅr);
        return new VurderVarigEndretArbeidssituasjonHåndteringDto(varigEndringDto);
    }

}
