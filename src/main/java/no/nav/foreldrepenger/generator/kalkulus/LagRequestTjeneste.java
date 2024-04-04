package no.nav.foreldrepenger.generator.kalkulus;

import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.HentBeregningsgrunnlagGUIRequest;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.HentBeregningsgrunnlagRequestDto;
import no.nav.folketrygdloven.kalkulus.håndtering.v1.HåndterBeregningDto;
import no.nav.folketrygdloven.kalkulus.kodeverk.BeregningSteg;
import no.nav.folketrygdloven.kalkulus.request.v1.HåndterBeregningListeRequest;
import no.nav.folketrygdloven.kalkulus.request.v1.HåndterBeregningRequest;

public class LagRequestTjeneste {

    private LagRequestTjeneste() {
        // Skal ikkje instansieres
    }

    public static HåndterBeregningListeRequest lagHåndterListeRequest(BeregnRequestDto request, HåndterBeregningDto håndterBeregningDto) {
        return null;
    }

    public static HåndterBeregningRequest lagHåndterRequest(BeregnRequestDto request, HåndterBeregningDto håndterBeregningDto) {
        return null;
    }

    public static BeregnRequestDto getFortsettBeregningListeRequest(BeregnRequestDto request, BeregningSteg stegType) {
        return new BeregnRequestDto(
                request.saksnummer(),
                request.behandlingUuid(),
                request.aktør(),
                request.ytelseSomSkalBeregnes(),
                stegType,
                request.kalkulatorInput());
    }

    public static HentBeregningsgrunnlagGUIRequest getHentGUIListeRequest(BeregnRequestDto request) {
        return new HentBeregningsgrunnlagGUIRequest(request.behandlingUuid(), request.kalkulatorInput());
    }

    public static HentBeregningsgrunnlagRequestDto getHentDetaljertListeRequest(BeregnRequestDto request) {
        return new HentBeregningsgrunnlagRequestDto(request.behandlingUuid());
    }
}
