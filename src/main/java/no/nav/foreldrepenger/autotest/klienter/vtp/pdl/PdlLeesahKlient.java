package no.nav.foreldrepenger.autotest.klienter.vtp.pdl;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.send;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.tilJsonOgPubliserIAllureRapport;

import java.net.http.HttpRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.foreldrepenger.vtp.kontrakter.PersonhendelseDto;

public class PdlLeesahKlient {

    private static final String PDL_LEESAH = "/pdl/leesah";
    private static final Logger LOG = LoggerFactory.getLogger(PdlLeesahKlient.class);

    @Step("Sender inn {personhendelseDto.type}")
    public void opprettHendelse(PersonhendelseDto personhendelseDto) {
        tilJsonOgPubliserIAllureRapport(personhendelseDto);
        LOG.info("Legger til hendelse av type: {} i PDL", personhendelseDto.getType());
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(PDL_LEESAH)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(personhendelseDto)));
        send(request.build());
    }
}
