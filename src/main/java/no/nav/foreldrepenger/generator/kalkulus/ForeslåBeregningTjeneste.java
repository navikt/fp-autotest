package no.nav.foreldrepenger.generator.kalkulus;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.FastsatteAndelerTidsbegrensetDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.FastsattePerioderTidsbegrensetDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.foreslå.FastsettBGTidsbegrensetArbeidsforholdDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.foreslå.FastsettBGTidsbegrensetArbeidsforholdHåndteringDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.foreslå.FastsettBeregningsgrunnlagATFLHåndteringDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.foreslå.InntektPrAndelDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.foreslå.VurderVarigEndringEllerNyoppstartetDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.foreslå.VurderVarigEndringEllerNyoppstartetSNHåndteringDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.typer.Periode;

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
                    .toList();
            return new FastsattePerioderTidsbegrensetDto(periodeEntry.getKey().getFom(), periodeEntry.getKey().getTom(), fastsatteAndeler);
        }).toList();
        return new FastsettBGTidsbegrensetArbeidsforholdHåndteringDto(
                new FastsettBGTidsbegrensetArbeidsforholdDto(fastsatteTidsbegrensetPerioder, frilansInntekt)
        );
    }

    public static FastsettBeregningsgrunnlagATFLHåndteringDto fastsettInntektVedAvvik(Map<Long, Integer> årsinntektPrArbeidstakerAndel) {
        var inntektPrAndelList = årsinntektPrArbeidstakerAndel.entrySet().stream()
                .map(e -> new InntektPrAndelDto(e.getValue(), e.getKey()))
                .toList();
        return new FastsettBeregningsgrunnlagATFLHåndteringDto(inntektPrAndelList, null, List.of());
    }

    public static FastsettBeregningsgrunnlagATFLHåndteringDto fastsettInntektVedAvvik(Map<Long, Integer> årsinntektPrArbeidstakerAndel, Integer frilansinnekt) {
        var inntektPrAndelList = årsinntektPrArbeidstakerAndel.entrySet().stream()
                .map(e -> new InntektPrAndelDto(e.getValue(), e.getKey()))
                .toList();
        return new FastsettBeregningsgrunnlagATFLHåndteringDto(inntektPrAndelList, frilansinnekt, List.of());
    }

    public static FastsettBeregningsgrunnlagATFLHåndteringDto fastsettInntektVedAvvik(Integer frilansinntekt) {
        return new FastsettBeregningsgrunnlagATFLHåndteringDto(Collections.emptyList(), frilansinntekt, List.of());
    }

    public static VurderVarigEndringEllerNyoppstartetSNHåndteringDto fastsettInntektVarigEndring(Integer beløpPrÅr, boolean erVarigEndring) {
        var varigEndringDto = new VurderVarigEndringEllerNyoppstartetDto(erVarigEndring, beløpPrÅr);
        return new VurderVarigEndringEllerNyoppstartetSNHåndteringDto(varigEndringDto);
    }
}
