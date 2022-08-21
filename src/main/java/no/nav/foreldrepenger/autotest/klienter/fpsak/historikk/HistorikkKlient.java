package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class HistorikkKlient {

    private static final String HISTORIKK_URL_FORMAT = "/historikk";

    public List<HistorikkInnslag> hentHistorikk(Saksnummer saksnummer) {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.FPSAK_BASE).path(HISTORIKK_URL_FORMAT)
                        .queryParam("saksnummer", saksnummer.value())
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<HistorikkInnslag>>() {}))
                .orElse(List.of());
    }
}
