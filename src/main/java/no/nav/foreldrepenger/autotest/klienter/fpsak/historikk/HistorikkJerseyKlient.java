package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;

public class HistorikkJerseyKlient extends FpsakJerseyKlient {

    private static final String HISTORIKK_URL_FORMAT = "/historikk";

    public HistorikkJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }

    @Step("Henter liste av historiske innslag")
    public List<HistorikkInnslag> hentHistorikk(long saksnummer) {
        return Optional.ofNullable(client.target(base)
                .path(HISTORIKK_URL_FORMAT)
                .queryParam("saksnummer", saksnummer)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<List<HistorikkInnslag>>() {}))
                .orElse(List.of());
    }

}
