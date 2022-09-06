package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import static jakarta.ws.rs.core.UriBuilder.fromUri;
import static no.nav.foreldrepenger.autotest.klienter.JacksonBodyHandlers.toJson;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.getRequestBuilder;
import static no.nav.foreldrepenger.autotest.klienter.JavaHttpKlient.send;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.tilJsonOgPubliserIAllureRapport;

import java.net.http.HttpRequest;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.klienter.BaseUriProvider;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public class TestscenarioKlient {

    private static final Logger LOG = LoggerFactory.getLogger(TestscenarioKlient.class);

    private static final String TESTSCENARIO_I_AUTOTEST_POST_URL = "/testscenarios";
    private static final TestscenarioHenter testscenarioHenter = new TestscenarioHenter();

    @Step("Oppretter familie/scenario #{key}")
    public TestscenarioDto opprettTestscenarioMedAktorId(String key, String aktorId, String ident) {
        var testscenarioObject = testscenarioHenter.hentScenario(key);

        var uriBuilder = fromUri(BaseUriProvider.VTP_BASE).path(TESTSCENARIO_I_AUTOTEST_POST_URL);
        if (aktorId != null) {
            uriBuilder = uriBuilder.queryParam("aktor1", aktorId);
        }
        if (ident != null) {
            uriBuilder = uriBuilder.queryParam("ident1", ident);
        }
        var request = getRequestBuilder()
                .uri(uriBuilder.build())
                .POST(HttpRequest.BodyPublishers.ofString(toJson(testscenarioObject)));
        var testscenarioDto = send(request.build(), TestscenarioDto.class, TestscenarioObjectMapper.DEFAULT_MAPPER_VTP);
        tilJsonOgPubliserIAllureRapport(testscenarioObject);
        LOG.info("Testscenario opprettet: [{}] med hovedsøker: {} annenpart: {}", key,
                testscenarioDto.personopplysninger().søkerIdent(), testscenarioDto.personopplysninger().annenpartIdent());
        return testscenarioDto;
    }

    @Step("Oppretter familie/scenario #{key}")
    public TestscenarioDto opprettTestscenario(String key) {
        return opprettTestscenarioMedAktorId(key, null, null);
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
