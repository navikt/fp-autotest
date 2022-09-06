package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class HistorikkKlientFelles implements HistorikkKlient {

    private static final String HISTORIKK_URL_FORMAT = "/historikk";

    private final URI baseUrl;

    public HistorikkKlientFelles(URI baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public List<HistorikkInnslag> hentHistorikk(Saksnummer saksnummer) {
        var request = getRequestBuilder()
                .uri(fromUri(baseUrl).path(HISTORIKK_URL_FORMAT)
                        .queryParam("saksnummer", saksnummer.value())
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<HistorikkInnslag>>() {}))
                .orElse(List.of());
    }
}
