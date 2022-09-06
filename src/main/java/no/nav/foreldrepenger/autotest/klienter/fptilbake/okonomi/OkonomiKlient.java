package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;
import java.util.UUID;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.BeregningResultat;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;

public class OkonomiKlient {

    private static final String GRUNNLAG_URL = "/grunnlag";
    private static final String BEREGNING_RESULTAT_URL = "/beregning/resultat";

    @Step
    public void putGrunnlag(Kravgrunnlag kravgrunnlag, int behandlingId) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(GRUNNLAG_URL)
                        .queryParam("behandlingId", behandlingId)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(kravgrunnlag)));
        send(request.build());
    }

    @Step
    public BeregningResultat hentResultat(UUID uuid){
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPTILBAKE_BASE)
                        .path(BEREGNING_RESULTAT_URL)
                        .queryParam("uuid", uuid)
                        .build())
                .GET();
        return send(request.build(), BeregningResultat.class);
    }
}
