package no.nav.foreldrepenger.autotest.klienter.vtp.tilbakekreving;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedBasicHeadere;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;

import java.net.http.HttpRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.vtp.kontrakter.TilbakekrevingKonsistensDto;

public class VTPTilbakekrevingKlient {

    private static final String TILBAKEKREVING_KONSISTENS = "/tilbakekreving/konsistens";
    private static final Logger LOG = LoggerFactory.getLogger(VTPTilbakekrevingKlient.class);


    @Step("Oppdaterer VTPs tilbakekrevingsmock med siste saksnummer og behandling")
    public void oppdaterTilbakekrevingKonsistens(Saksnummer saksnummer, int behandlingId) {
        LOG.info("Oppdaterer VTPs tilbakekrevingsmock med saksnummer {} og behandling {} for konsistens", saksnummer.value(), behandlingId);

        var request = requestMedBasicHeadere()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(TILBAKEKREVING_KONSISTENS)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(
                        new TilbakekrevingKonsistensDto(saksnummer.value(), ""+behandlingId))));
        send(request.build());
    }
}
