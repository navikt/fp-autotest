package no.nav.foreldrepenger.generator.kalkulus;


import static no.nav.folketrygdloven.kalkulus.kodeverk.AktivitetStatus.ARBEIDSTAKER;
import static no.nav.folketrygdloven.kalkulus.kodeverk.AktivitetStatus.FRILANSER;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.BeregningsgrunnlagDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelBeregnRequestDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelHåndterBeregningRequestDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.ArbeidstakerandelUtenIMMottarYtelseDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.FaktaBeregningLagreDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.FaktaOmBeregningHåndteringDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.MottarYtelseDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.RefusjonskravPrArbeidsgiverVurderingDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.VurderATogFLiSammeOrganisasjonAndelDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.fakta.VurderATogFLiSammeOrganisasjonDto;

public class FaktaOmBeregningTjeneste {

    private FaktaOmBeregningTjeneste() {
        // Skal ikkje instansieres
    }

    public static EnkelHåndterBeregningRequestDto  lagFaktaOmBeregningHåndterRequest(EnkelBeregnRequestDto request, FaktaBeregningLagreDto fakta) {
        return lagHåndterRequest(request, new FaktaOmBeregningHåndteringDto(fakta));
    }

    public static List<RefusjonskravPrArbeidsgiverVurderingDto> vurderRefusjonskravGyldighet(Map<String, Boolean> skalUtvideGyldighet) {
        return skalUtvideGyldighet.entrySet().stream()
                .map(e -> new RefusjonskravPrArbeidsgiverVurderingDto(e.getKey(), e.getValue()))
                .toList();
    }

    public static VurderATogFLiSammeOrganisasjonDto lagATFLISammeOrgDto(Map<Long, Integer> beløpMap) {
        var andelDtoList = beløpMap.entrySet().stream()
                .filter(a -> beløpMap.containsKey(a.getKey()))
                .map(a -> new VurderATogFLiSammeOrganisasjonAndelDto(a.getKey(), a.getValue()))
                .toList();
        return new VurderATogFLiSammeOrganisasjonDto(andelDtoList);
    }


    public static MottarYtelseDto lagMottarYtelseDto(Map<Long, Boolean> mottarYtelseMap, BeregningsgrunnlagDto beregningsgrunnlagDto) {
        var andeler = beregningsgrunnlagDto.getBeregningsgrunnlagPeriode().getFirst().getBeregningsgrunnlagPrStatusOgAndel();
        List<ArbeidstakerandelUtenIMMottarYtelseDto> mottarYtelseDtoList = andeler.stream()
                .filter(a -> mottarYtelseMap.containsKey(a.getAndelsnr()))
                .filter(a -> a.getAktivitetStatus().equals(ARBEIDSTAKER))
                .map(a -> new ArbeidstakerandelUtenIMMottarYtelseDto(a.getAndelsnr(), mottarYtelseMap.get(a.getAndelsnr())))
                .toList();
        Optional<Boolean> frilansMottarYtelse = andeler.stream()
                .filter(a -> a.getAktivitetStatus().equals(FRILANSER))
                .findFirst()
                .map(a -> mottarYtelseMap.get(a.getAndelsnr()));
        return new MottarYtelseDto(frilansMottarYtelse.orElse(null), mottarYtelseDtoList);
    }

}
