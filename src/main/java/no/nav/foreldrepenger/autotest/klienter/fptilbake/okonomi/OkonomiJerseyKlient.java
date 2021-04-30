package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi;

import static jakarta.ws.rs.client.Entity.json;

import java.util.UUID;

import io.qameta.allure.Step;
import jakarta.ws.rs.client.ClientRequestFilter;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.FptilbakeJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.BeregningResultat;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;

public class OkonomiJerseyKlient extends FptilbakeJerseyKlient {

    private static final String GRUNNLAG_URL = "/grunnlag";
    private static final String BEREGNING_RESULTAT_URL = "/beregning/resultat";

    public OkonomiJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }

    @Step
    public void putGrunnlag(Kravgrunnlag kravgrunnlag, int behandlingId) {
        client.target(base)
                .path(GRUNNLAG_URL)
                .queryParam("behandlingId", behandlingId)
                .request()
                .post(json(kravgrunnlag));
    }

    @Step
    public BeregningResultat hentResultat(UUID uuid){
        return client.target(base)
                .path(BEREGNING_RESULTAT_URL)
                .queryParam("uuid", uuid)
                .request()
                .get(BeregningResultat.class);
    }
}
