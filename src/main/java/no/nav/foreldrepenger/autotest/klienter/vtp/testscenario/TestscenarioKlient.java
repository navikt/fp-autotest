package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedBasicHeadere;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugJson;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.vtp.kontrakter.v2.PersonDto;
import no.nav.foreldrepenger.vtp.kontrakter.v2.TilordnetIdentDto;
import tools.jackson.core.type.TypeReference;

public class TestscenarioKlient {

    private static final Logger LOG = LoggerFactory.getLogger(TestscenarioKlient.class);

    @Step("Oppretter familie/scenario")
    public List<TilordnetIdentDto> opprettTestscenario(List<PersonDto> personer) {
        var personerJson = toJson(personer);
        var request = requestMedBasicHeadere()
                .uri(fromUri(BaseUriProvider.VTP_API_BASE).path("/testscenarios/v2/opprett").build())
                .POST(HttpRequest.BodyPublishers.ofString(personerJson));
        var identer = Optional.ofNullable(send(request.build(), new TypeReference<List<TilordnetIdentDto>>() {})).orElseGet(List::of);
        debugJson(personerJson);
        LOG.info("Testscenario opprettet med identer: {} ", identer.stream().map(TilordnetIdentDto::fnr).toList());
        return identer;
    }

    public List<String> hentAlleIdenterFraAlleScenarier() {
        var request = requestMedBasicHeadere()
                .uri(fromUri(BaseUriProvider.VTP_API_BASE)
                        .path("/testscenarios/v2/alleidenter")
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<String>>() {})).orElseGet(List::of);
    }
}
