package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak;

import static jakarta.ws.rs.client.Entity.json;
import static no.nav.foreldrepenger.common.mapper.DefaultJsonMapper.MAPPER;

import java.util.List;

import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Sok;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

public class FagsakJerseyKlient extends FpsakJerseyKlient {

    private static final String FAGSAK_URL = "/fagsak";
    private static final String FAGSAK_SØK_URL = FAGSAK_URL + "/sok";

    public FagsakJerseyKlient(ClientRequestFilter filter) {
        super(MAPPER, filter);
    }

    public Fagsak hentFagsak(String saksnummer) {
        return client.target(base)
                .path(FAGSAK_URL)
                .queryParam("saksnummer", saksnummer)
                .request()
                .get(Fagsak.class);
    }

    public List<Fagsak> søk(Fødselsnummer fnr) {
        return søk(new Sok(fnr.value()));
    }

    public List<Fagsak> søk(String søk) {
        return søk(new Sok(søk));
    }

    private List<Fagsak> søk(Sok søk) {
        return client.target(base)
                .path(FAGSAK_SØK_URL)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(json(søk), Response.class)
                .readEntity(new GenericType<>() {});
    }
}
