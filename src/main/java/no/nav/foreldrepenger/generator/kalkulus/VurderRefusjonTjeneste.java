package no.nav.foreldrepenger.generator.kalkulus;


import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterRequest;

import java.time.LocalDate;
import java.util.Collections;

import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.HåndterBeregningRequestDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.refusjon.VurderRefusjonAndelBeregningsgrunnlagDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.refusjon.VurderRefusjonBeregningsgrunnlagDto;

// TODO: sjekk responser
public class VurderRefusjonTjeneste {

    private VurderRefusjonTjeneste() {
        // Skal ikkje instansieres
    }

    public static HåndterBeregningRequestDto lagVurderRefusjonRequest(BeregnRequestDto request, VurderRefusjonBeregningsgrunnlagDto dto) {
        return lagHåndterRequest(request, dto);
    }

    public static HåndterBeregningRequestDto lagVurderRefusjonRequest(BeregnRequestDto request, VurderRefusjonAndelBeregningsgrunnlagDto andelDto) {
        VurderRefusjonBeregningsgrunnlagDto dto = new VurderRefusjonBeregningsgrunnlagDto(Collections.singletonList(andelDto));
        return lagHåndterRequest(request, dto);
    }

    public static VurderRefusjonAndelBeregningsgrunnlagDto lagVurderRefusjonAndelDto(String agIdent, String arbeidsforholdRef, LocalDate fastsattRefusjonFOM) {
        return new VurderRefusjonAndelBeregningsgrunnlagDto(agIdent, null, arbeidsforholdRef, fastsattRefusjonFOM, null);
    }

    public static VurderRefusjonAndelBeregningsgrunnlagDto lagVurderRefusjonAndelDto(String agIdent, String arbeidsforholdRef, LocalDate fastsattRefusjonFOM, int delvisRefusjonBeløp) {
        return new VurderRefusjonAndelBeregningsgrunnlagDto(agIdent, null, arbeidsforholdRef, fastsattRefusjonFOM, delvisRefusjonBeløp);
    }

}
