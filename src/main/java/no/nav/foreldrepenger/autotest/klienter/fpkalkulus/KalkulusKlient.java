package no.nav.foreldrepenger.autotest.klienter.fpkalkulus;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.time.Duration;

import io.qameta.allure.Step;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.BeregnRequestDto;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.EnkelFpkalkulusRequestDto;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.HentBeregningsgrunnlagGUIRequest;
import no.nav.folketrygdloven.fpkalkulus.kontrakt.HåndterBeregningRequestDto;
import no.nav.folketrygdloven.kalkulus.response.v1.TilstandResponse;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.detaljert.BeregningsgrunnlagGrunnlagDto;
import no.nav.folketrygdloven.kalkulus.response.v1.beregningsgrunnlag.gui.BeregningsgrunnlagDto;
import no.nav.folketrygdloven.kalkulus.response.v1.håndtering.OppdateringRespons;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;

public class KalkulusKlient {
    private static final String KLIENT_ID = "kalkulus";
    private static final String KALKULUS_DETALJERT_GRUNNLAG_URL = "/grunnlag";
    private static final String KALKULUS_GUI_GRUNNLAG_URL = "/grunnlag/gui";
    private static final String KALKULUS_BEREGN_URL = "/beregn";
    private static final String KALKULUS_AVKLARINGSBEHOV_URL = "/avklaringsbehov";

    private static final Duration KALKULUS_TIMEOUT = Duration.ofSeconds(10);

    private final SaksbehandlerRolle saksbehandlerRolle;

    public KalkulusKlient(SaksbehandlerRolle saksbehandlerRolle) {
        this.saksbehandlerRolle = saksbehandlerRolle;
    }


    public BeregningsgrunnlagGrunnlagDto hentDetaljertBeregningsgrunnlag(EnkelFpkalkulusRequestDto hentBeregningsgrunnlagRequest) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle, KLIENT_ID)
                .timeout(KALKULUS_TIMEOUT)
                .uri(fromUri(BaseUriProvider.KALKULUS_BASE)
                        .path(KALKULUS_DETALJERT_GRUNNLAG_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(hentBeregningsgrunnlagRequest)));
        return send(request.build(), BeregningsgrunnlagGrunnlagDto.class);
    }

    public BeregningsgrunnlagDto hentGUIBeregningsgrunnlag(HentBeregningsgrunnlagGUIRequest hentBeregningsgrunnlagGUIRequest) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle, KLIENT_ID)
                .timeout(KALKULUS_TIMEOUT)
                .uri(fromUri(BaseUriProvider.KALKULUS_BASE)
                        .path(KALKULUS_GUI_GRUNNLAG_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(hentBeregningsgrunnlagGUIRequest)));
        return send(request.build(), BeregningsgrunnlagDto.class);
    }


    public TilstandResponse kjørBeregning(BeregnRequestDto beregnRequestDto) {
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle, KLIENT_ID)
                .timeout(KALKULUS_TIMEOUT)
                .uri(fromUri(BaseUriProvider.KALKULUS_BASE)
                        .path(KALKULUS_BEREGN_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(beregnRequestDto)));
        return send(request.build(), TilstandResponse.class);
    }

    @Step("Håndter aksjonspunkt")
    public OppdateringRespons håndterBeregning(HåndterBeregningRequestDto håndterRequestDto) {
        håndterRequestDto.håndterBeregningDtoList().forEach(dto -> dto.setBegrunnelse("Løst av verdikjeden"));
        var request = requestMedInnloggetSaksbehandler(saksbehandlerRolle, KLIENT_ID)
                .timeout(KALKULUS_TIMEOUT)
                .uri(fromUri(BaseUriProvider.KALKULUS_BASE)
                        .path(KALKULUS_AVKLARINGSBEHOV_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(håndterRequestDto)));
        return send(request.build(), OppdateringRespons.class);
    }
}
