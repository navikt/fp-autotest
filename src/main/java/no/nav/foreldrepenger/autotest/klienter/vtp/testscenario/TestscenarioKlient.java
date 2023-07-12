package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestProvider.requestMedBasicHeadere;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.tilJsonOgPubliserIAllureRapport;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import no.nav.foreldrepenger.vtp.kontrakter.v2.PersonDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public class TestscenarioKlient {

    private static final Logger LOG = LoggerFactory.getLogger(TestscenarioKlient.class);

    private static final String TESTSCENARIO_I_AUTOTEST_POST_URL = "/testscenarios";

    @Step("Oppretter familie/scenario")
    public TestscenarioDto opprettTestscenario(List<PersonDto> personer) {
        var request = requestMedBasicHeadere()
                .uri(fromUri(BaseUriProvider.VTP_BASE).path("/testscenarios/v2").build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(personer)));
        var testscenarioDto = send(request.build(), TestscenarioDto.class, TestscenarioObjectMapper.DEFAULT_MAPPER_VTP);
//        tilJsonOgPubliserIAllureRapport(testscenarioObject);
        LOG.info("Testscenario opprettet med hovedsøker: {} annenpart: {}", testscenarioDto.personopplysninger().søkerIdent(), testscenarioDto.personopplysninger().annenpartIdent());
        return testscenarioDto;
    }

    public List<TestscenarioDto> hentAlleScenarier() {
        var request = requestMedBasicHeadere()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<TestscenarioDto>>() {}))
                .orElse(List.of());
    }
}
