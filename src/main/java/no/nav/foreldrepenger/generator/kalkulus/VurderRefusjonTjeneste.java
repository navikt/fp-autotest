package no.nav.foreldrepenger.generator.kalkulus;


import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterListeRequest;

import java.time.LocalDate;
import java.util.Collections;

import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.refusjon.VurderRefusjonAndelBeregningsgrunnlagDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.refusjon.VurderRefusjonBeregningsgrunnlagDto;
import no.nav.folketrygdloven.kalkulus.request.v1.HåndterBeregningListeRequest;

// TODO: sjekk responser
public class VurderRefusjonTjeneste {

    private VurderRefusjonTjeneste() {
        // Skal ikkje instansieres
    }

    public static HåndterBeregningListeRequest lagVurderRefusjonRequest(BeregnRequestDto request, VurderRefusjonBeregningsgrunnlagDto dto) {
        return lagHåndterListeRequest(request, dto);
    }

    public static HåndterBeregningListeRequest lagVurderRefusjonRequest(BeregnRequestDto request, VurderRefusjonAndelBeregningsgrunnlagDto andelDto) {
        VurderRefusjonBeregningsgrunnlagDto dto = new VurderRefusjonBeregningsgrunnlagDto(Collections.singletonList(andelDto));
        return lagHåndterListeRequest(request, dto);
    }

    public static VurderRefusjonAndelBeregningsgrunnlagDto lagVurderRefusjonAndelDto(String agIdent, String arbeidsforholdRef, LocalDate fastsattRefusjonFOM) {
        return new VurderRefusjonAndelBeregningsgrunnlagDto(agIdent, null, arbeidsforholdRef, fastsattRefusjonFOM, null);
    }

    public static VurderRefusjonAndelBeregningsgrunnlagDto lagVurderRefusjonAndelDto(String agIdent, String arbeidsforholdRef, LocalDate fastsattRefusjonFOM, int delvisRefusjonBeløp) {
        return new VurderRefusjonAndelBeregningsgrunnlagDto(agIdent, null, arbeidsforholdRef, fastsattRefusjonFOM, delvisRefusjonBeløp);
    }

}
