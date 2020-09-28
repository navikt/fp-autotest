package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.FptilbakeKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingIdBasicDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.BeregningResultat;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;

import java.util.UUID;

public class OkonomiKlient extends FptilbakeKlient {

    private static final String GRUNNLAG_URL = "/grunnlag?behandlingId=";
    private static final String BEREGNING_RESULTAT_URL = "/beregning/resultat?uuid=";

    public OkonomiKlient(HttpSession session) {
        super(session);
    }

    @Step
    public void putGrunnlag(Kravgrunnlag kravgrunnlag, int behandlingId) {
        String url = hentRestRotUrl() + GRUNNLAG_URL + behandlingId;
        postOgVerifiser(url, kravgrunnlag, StatusRange.STATUS_SUCCESS);
    }

    @Step
    public BeregningResultat hentResultat(UUID uuid){
        String url = hentRestRotUrl() + BEREGNING_RESULTAT_URL + uuid;
        return getOgHentJson(url, BeregningResultat.class, StatusRange.STATUS_SUCCESS);
    }
}
