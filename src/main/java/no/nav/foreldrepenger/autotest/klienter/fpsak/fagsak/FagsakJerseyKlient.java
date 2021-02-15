package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak;

import static javax.ws.rs.client.Entity.json;

import java.util.List;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Sok;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Status;

public class FagsakJerseyKlient extends FpsakJerseyKlient {

    private static final String FAGSAK_URL = "/fagsak";
    private static final String STATUS_URL = FAGSAK_URL + "/status";
    private static final String FAGSAK_SØK_URL = FAGSAK_URL + "/sok";

    public FagsakJerseyKlient(ClientRequestFilter filter) {
        super(filter);
    }

    public Status status(int saksnummer, int gruppe) {
        return client.target(base)
                .path(STATUS_URL)
                .queryParam("saksnummer", saksnummer)
                .queryParam("gruppe", gruppe)
                .request()
                .get(Status.class);
    }

    @Step("Henter fagsak {saksnummer}")
    public Fagsak getFagsak(String saksnummer) {
        return client.target(base)
                .path(FAGSAK_URL)
                .queryParam("saksnummer", saksnummer)
                .request()
                .get(Fagsak.class);
    }

    @Step("Søker etter fagsak {søk}")
    public List<Fagsak> søk(String søk) {
        return søk(new Sok(søk));
    }

    public List<Fagsak> søk(Sok søk) {
        return client.target(base)
                .path(FAGSAK_SØK_URL)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(json(søk), Response.class)
                .readEntity(new GenericType<>() {});
    }
}
