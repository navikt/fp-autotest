package no.nav.foreldrepenger.generator.kalkulus;

import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelBeregnRequestDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelFpkalkulusRequestDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelHentBeregningsgrunnlagGUIRequest;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.EnkelHåndterBeregningRequestDto;
import no.nav.foreldrepenger.kalkulus.kontrakt.request.håndtering.HåndterBeregningDto;

public class LagRequestTjeneste {

    private LagRequestTjeneste() {
        // Skal ikkje instansieres
    }

    public static EnkelHåndterBeregningRequestDto lagHåndterRequest(EnkelBeregnRequestDto request, HåndterBeregningDto håndterBeregningDto) {
        return new EnkelHåndterBeregningRequestDto(request.behandlingUuid(), request.saksnummer(), request.kalkulatorInput(), håndterBeregningDto);
    }

    public static EnkelBeregnRequestDto getFortsettBeregningRequest(EnkelBeregnRequestDto request, BeregningSteg stegType) {
        return new EnkelBeregnRequestDto(
                request.saksnummer(),
                request.behandlingUuid(),
                request.aktør(),
                request.ytelseSomSkalBeregnes(),
                stegType,
                request.kalkulatorInput(),
                null);
    }

    public static EnkelHentBeregningsgrunnlagGUIRequest getHentGUIRequest(EnkelBeregnRequestDto request) {
        return new EnkelHentBeregningsgrunnlagGUIRequest(request.behandlingUuid(), request.saksnummer(), request.kalkulatorInput());
    }

    public static EnkelFpkalkulusRequestDto getHentDetaljertRequest(EnkelBeregnRequestDto request) {
        return new EnkelFpkalkulusRequestDto(request.behandlingUuid(), request.saksnummer());
    }
}
