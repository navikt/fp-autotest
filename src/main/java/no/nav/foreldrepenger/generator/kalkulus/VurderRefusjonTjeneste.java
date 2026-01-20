package no.nav.foreldrepenger.generator.kalkulus;


import static no.nav.foreldrepenger.generator.kalkulus.LagRequestTjeneste.lagHåndterRequest;

import java.time.LocalDate;
import java.util.Collections;

import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelBeregnRequestDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelHåndterBeregningRequestDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.refusjon.VurderRefusjonAndelBeregningsgrunnlagDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.refusjon.VurderRefusjonBeregningsgrunnlagDto;

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
