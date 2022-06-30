package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk;

import static no.nav.foreldrepenger.common.mapper.DefaultJsonMapper.MAPPER;

import java.util.List;
import java.util.Optional;

import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.autotest.klienter.fpsak.FpsakJerseyKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;

public class HistorikkJerseyKlient extends FpsakJerseyKlient {

    private static final String HISTORIKK_URL_FORMAT = "/historikk";

    public HistorikkJerseyKlient(ClientRequestFilter filter) {
        super(MAPPER, filter);
    }

    public List<HistorikkInnslag> hentHistorikk(String saksnummer) {
        return Optional.ofNullable(client.target(base)
                .path(HISTORIKK_URL_FORMAT)
                .queryParam("saksnummer", saksnummer)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<List<HistorikkInnslag>>() {}))
                .orElse(List.of());
    }
}
