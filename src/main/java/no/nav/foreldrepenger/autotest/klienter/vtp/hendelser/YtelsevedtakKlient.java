package no.nav.foreldrepenger.autotest.klienter.vtp.hendelser;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedBasicHeadere;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.tilJsonOgPubliserIAllureRapport;

import java.net.http.HttpRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.vtp.kontrakter.hendelser.YtelsevedtakDto;

public class YtelsevedtakKlient {

    private static final String YTELSEVEDTAK_PATH = "/hendelser/ytelsevedtak";
    private static final Logger LOG = LoggerFactory.getLogger(YtelsevedtakKlient.class);

    @Step("Sender inn ytelsevedtak")
    public void sendYtelsevedtak(YtelsevedtakDto ytelsevedtakDto) {
        tilJsonOgPubliserIAllureRapport(ytelsevedtakDto);
        LOG.info("Sender inn {} vedtak for fnr {} ({} - {})", ytelsevedtakDto.ytelseType(), ytelsevedtakDto.fnr(),
                ytelsevedtakDto.fom(), ytelsevedtakDto.tom());
        var request = requestMedBasicHeadere().uri(fromUri(BaseUriProvider.VTP_API_BASE).path(YTELSEVEDTAK_PATH).build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(ytelsevedtakDto)));
        send(request.build());
    }
}
