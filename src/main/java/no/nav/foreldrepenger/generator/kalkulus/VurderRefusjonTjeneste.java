package no.nav.foreldrepenger.generator.kalkulus;


import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterRequest;

import java.time.LocalDate;
import java.util.Collections;

import no.nav.folketrygdloven.kalkulus.håndtering.v1.refusjon.VurderRefusjonAndelBeregningsgrunnlagDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.refusjon.VurderRefusjonBeregningsgrunnlagDto;
import no.nav.folketrygdloven.kalkulus.request.v1.enkel.EnkelBeregnRequestDto;
import no.nav.folketrygdloven.kalkulus.request.v1.enkel.EnkelHåndterBeregningRequestDto;

// TODO: sjekk responser
public class VurderRefusjonTjeneste {

    private VurderRefusjonTjeneste() {
        // Skal ikkje instansieres
    }

    public static EnkelHåndterBeregningRequestDto  lagVurderRefusjonRequest(EnkelBeregnRequestDto request, VurderRefusjonBeregningsgrunnlagDto dto) {
        return lagHåndterRequest(request, dto);
    }

    public static EnkelHåndterBeregningRequestDto  lagVurderRefusjonRequest(EnkelBeregnRequestDto request, VurderRefusjonAndelBeregningsgrunnlagDto andelDto) {
        VurderRefusjonBeregningsgrunnlagDto dto = new VurderRefusjonBeregningsgrunnlagDto(Collections.singletonList(andelDto), null);
        return lagHåndterRequest(request, dto);
    }

    public static VurderRefusjonAndelBeregningsgrunnlagDto lagVurderRefusjonAndelDto(String agIdent, String arbeidsforholdRef, LocalDate fastsattRefusjonFOM) {
        return new VurderRefusjonAndelBeregningsgrunnlagDto(agIdent, null, arbeidsforholdRef, fastsattRefusjonFOM, null);
    }

    public static VurderRefusjonAndelBeregningsgrunnlagDto lagVurderRefusjonAndelDto(String agIdent, String arbeidsforholdRef, LocalDate fastsattRefusjonFOM, int delvisRefusjonBeløp) {
        return new VurderRefusjonAndelBeregningsgrunnlagDto(agIdent, null, arbeidsforholdRef, fastsattRefusjonFOM, delvisRefusjonBeløp);
    }

}
