package no.nav.foreldrepenger.generator.kalkulus;

import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.EnkelFpkalkulusRequestDto;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.HentBeregningsgrunnlagGUIRequest;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.HåndterBeregningRequestDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.HåndterBeregningDto;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;

import java.util.Collections;

public class LagRequestTjeneste {

    private LagRequestTjeneste() {
        // Skal ikkje instansieres
    }

    public static HåndterBeregningRequestDto lagHåndterListeRequest(BeregnRequestDto request, HåndterBeregningDto håndterBeregningDto) {
        return new HåndterBeregningRequestDto(request.behandlingUuid(), request.kalkulatorInput(), Collections.singletonList(håndterBeregningDto));
    }

    public static HåndterBeregningRequestDto lagHåndterRequest(BeregnRequestDto request, HåndterBeregningDto håndterBeregningDto) {
        return new HåndterBeregningRequestDto(request.behandlingUuid(), request.kalkulatorInput(), Collections.singletonList(håndterBeregningDto));
    }

    public static BeregnRequestDto getFortsettBeregningListeRequest(BeregnRequestDto request, BeregningSteg stegType) {
        return new BeregnRequestDto(
                request.saksnummer(),
                request.behandlingUuid(),
                request.aktør(),
                request.ytelseSomSkalBeregnes(),
                stegType,
                request.kalkulatorInput(),
                null);
    }

    public static HentBeregningsgrunnlagGUIRequest getHentGUIListeRequest(BeregnRequestDto request) {
        return new HentBeregningsgrunnlagGUIRequest(request.behandlingUuid(), request.kalkulatorInput());
    }

    public static EnkelFpkalkulusRequestDto getHentDetaljertListeRequest(BeregnRequestDto request) {
        return new EnkelFpkalkulusRequestDto(request.behandlingUuid(), request.saksnummer());
    }
}
