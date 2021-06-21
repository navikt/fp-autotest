package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import static jakarta.ws.rs.client.Entity.json;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.tilJsonOgPubliserIAllureRapport;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.autotest.util.log.LoggFormater;
import no.nav.foreldrepenger.autotest.util.testscenario.TestscenarioHenter;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public class TestscenarioJerseyKlient extends VTPJerseyKlient {

    private static final Logger logger = LoggerFactory.getLogger(TestscenarioJerseyKlient.class);

    private static final String TESTSCENARIO_I_AUTOTEST_POST_URL = "/testscenarios";
    private static final TestscenarioHenter testscenarioHenter = new TestscenarioHenter();

    public TestscenarioJerseyKlient() {
        super();
    }

    @Step("Oppretter familie/scenario #{key}")
    public TestscenarioDto opprettTestscenarioMedAktorId(String key, String aktorId, String ident) {
        var testscenarioObject = testscenarioHenter.hentScenario(key);
        var testscenarioDto = client.target(base)
                .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                .queryParam("aktor1", aktorId)
                .queryParam("ident1", ident)
                .request()
                .post(json(testscenarioObject), TestscenarioDto.class);
        oppdaterCallIdOgLeggTilModellIAllureRapport(testscenarioObject);
        logger.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.personopplysninger().søkerIdent());
        return testscenarioDto;
    }

    @Step("Oppretter familie/scenario #{key}")
    public TestscenarioDto opprettTestscenario(String key) {
        var testscenarioObject = testscenarioHenter.hentScenario(key);
        var testscenarioDto = client.target(base)
                .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                .request()
                .post(json(testscenarioObject), TestscenarioDto.class);
        oppdaterCallIdOgLeggTilModellIAllureRapport(testscenarioObject);
        logger.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.personopplysninger().søkerIdent());
        return testscenarioDto;
    }

    private void oppdaterCallIdOgLeggTilModellIAllureRapport(Object testscenarioObject) {
        LoggFormater.setCallId();
        tilJsonOgPubliserIAllureRapport(testscenarioObject);
    }

    public List<TestscenarioDto> hentAlleScenarier() {
        return client.target(base)
                .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<>() {});
    }
}
