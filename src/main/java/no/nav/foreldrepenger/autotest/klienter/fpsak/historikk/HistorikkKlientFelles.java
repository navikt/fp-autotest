package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedInnloggetSaksbehandler;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;

import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class HistorikkKlientFelles implements HistorikkKlient {

    private static final String HISTORIKK_URL_FORMAT = "/historikk";

    private final URI baseUrl;
    private final SaksbehandlerRolle saksbehandlerRolle;

    private final String apiName;

    public HistorikkKlientFelles(URI baseUrl, SaksbehandlerRolle saksbehandlerRolle, String apiName) {
        this.baseUrl = baseUrl;
        this.saksbehandlerRolle = saksbehandlerRolle;
        this.apiName = apiName;
    }

    @Override
    public List<HistorikkInnslag> hentHistorikk(Saksnummer saksnummer) {
        return hentHistorikk(saksnummer, "");
    }

    public List<HistorikkInnslag> hentHistorikk(Saksnummer saksnummer, String path) {
        var request = requestMedInnloggetSaksbehandler(this.saksbehandlerRolle, this.apiName)
                .uri(fromUri(baseUrl).path(HISTORIKK_URL_FORMAT).path(path)
                        .queryParam("saksnummer", saksnummer.value())
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<HistorikkInnslag>>() {}))
                .orElse(List.of());
    }
}
