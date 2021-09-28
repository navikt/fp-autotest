package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak;

import static jakarta.ws.rs.client.Entity.json;

import java.util.List;

import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Sok;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Status;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

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


    public Fagsak getFagsak(String saksnummer) {
        return client.target(base)
                .path(FAGSAK_URL)
                .queryParam("saksnummer", saksnummer)
                .request()
                .get(Fagsak.class);
    }

    public List<Fagsak> søk(Fødselsnummer fnr) {
        return søk(new Sok(fnr.getFnr()));
    }

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
