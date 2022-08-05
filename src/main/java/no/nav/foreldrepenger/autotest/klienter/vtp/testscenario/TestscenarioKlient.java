package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.HttpRequestSender.send;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.tilJsonOgPubliserIAllureRapport;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public class TestscenarioKlient {

    private static final Logger LOG = LoggerFactory.getLogger(TestscenarioKlient.class);

    private static final String TESTSCENARIO_I_AUTOTEST_POST_URL = "/testscenarios";
    private static final TestscenarioHenter testscenarioHenter = new TestscenarioHenter();

    @Step("Oppretter familie/scenario #{key}")
    public TestscenarioDto opprettTestscenarioMedAktorId(String key, String aktorId, String ident) {
        var testscenarioObject = testscenarioHenter.hentScenario(key);

        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                        .queryParam("aktor1", aktorId)
                        .queryParam("ident1", ident)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(testscenarioObject)));
        var testscenarioDto = send(request.build(), TestscenarioDto.class, TestscenarioObjectMapper.DEFAULT_MAPPER_VTP);
        tilJsonOgPubliserIAllureRapport(testscenarioObject);
        LOG.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.personopplysninger().søkerIdent());
        return testscenarioDto;
    }

    @Step("Oppretter familie/scenario #{key}")
    public TestscenarioDto opprettTestscenario(String key) {
        var testscenarioObject = testscenarioHenter.hentScenario(key);
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JacksonBodyHandlers.toJson(testscenarioObject)));
        var testscenarioDto = send(request.build(), TestscenarioDto.class, TestscenarioObjectMapper.DEFAULT_MAPPER_VTP);
        tilJsonOgPubliserIAllureRapport(testscenarioObject);
        LOG.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key, testscenarioDto.personopplysninger().søkerIdent());
        return testscenarioDto;
    }

    public List<TestscenarioDto> hentAlleScenarier() {
        var request = getRequestBuilder()
                .uri(fromUri(BaseUriProvider.VTP_BASE)
                        .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                        .build())
                .GET();
        return Optional.ofNullable(send(request.build(), new TypeReference<List<TestscenarioDto>>() {}))
                .orElse(List.of());
    }
}
