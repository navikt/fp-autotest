package no.nav.foreldrepenger.generator.kalkulus;


import static no.nav.folketrygdloven.kalkulus.kodeverk.AktivitetStatus.ARBEIDSTAKER;
import static no.nav.folketrygdloven.kalkulus.kodeverk.AktivitetStatus.FRILANSER;
import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterListeRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fakta.ArbeidstakerandelUtenIMMottarYtelseDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fakta.FaktaBeregningLagreDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fakta.FaktaOmBeregningHåndteringDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fakta.MottarYtelseDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fakta.RefusjonskravPrArbeidsgiverVurderingDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fakta.VurderATogFLiSammeOrganisasjonAndelDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.fakta.VurderATogFLiSammeOrganisasjonDto;
import no.nav.folketrygdloven.kalkulus.request.v1.HåndterBeregningListeRequest;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.BeregningsgrunnlagDto;

public class FaktaOmBeregningTjeneste {

    private FaktaOmBeregningTjeneste() {
        // Skal ikkje instansieres
    }

    public static HåndterBeregningListeRequest lagFaktaOmBeregningHåndterRequest(BeregnRequestDto request, FaktaBeregningLagreDto fakta) {
        return lagHåndterListeRequest(request, new FaktaOmBeregningHåndteringDto(fakta));
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


    // TODO: Riktig BeregningsgrunnlagDto?
    public static MottarYtelseDto lagMottarYtelseDto(Map<Long, Boolean> mottarYtelseMap, BeregningsgrunnlagDto beregningsgrunnlagDto) {
        var andeler = beregningsgrunnlagDto.getBeregningsgrunnlagPeriode().get(0).getBeregningsgrunnlagPrStatusOgAndel();
        List<ArbeidstakerandelUtenIMMottarYtelseDto> mottarYtelseDtoList = andeler.stream()
                .filter(a -> mottarYtelseMap.containsKey(a.getAndelsnr()))
                .filter(a -> a.getAktivitetStatus().equals(ARBEIDSTAKER))
                .map(a -> new ArbeidstakerandelUtenIMMottarYtelseDto(a.getAndelsnr(), mottarYtelseMap.get(a.getAndelsnr())))
                .collect(Collectors.toList());
        Optional<Boolean> frilansMottarYtelse = andeler.stream()
                .filter(a -> a.getAktivitetStatus().equals(FRILANSER))
                .findFirst()
                .map(a -> mottarYtelseMap.get(a.getAndelsnr()));
        return new MottarYtelseDto(frilansMottarYtelse.orElse(null), mottarYtelseDtoList);
    }

}
