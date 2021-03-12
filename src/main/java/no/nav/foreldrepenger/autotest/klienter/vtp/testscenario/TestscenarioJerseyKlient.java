package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import static javax.ws.rs.client.Entity.json;

import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;
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

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i Autotest")
    public TestscenarioDto opprettTestscenarioMedAktorId(String key, String aktorId, String ident) {
        var testscenarioObject = testscenarioHenter.hentScenario(key);
        var testscenarioDto = client.target(base)
                .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                .queryParam("aktor1", aktorId)
                .queryParam("ident1", ident)
                .request()
                .post(json(testscenarioObject), TestscenarioDto.class);
        logger.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.personopplysninger().søkerIdent());
        return testscenarioDto;
    }

    @Step("Oppretter testscenario {id} fra Json fil lokalisert i Autotest")
    public TestscenarioDto opprettTestscenario(String key) {
        var testscenarioObject = testscenarioHenter.hentScenario(key);
        var testscenarioDto = client.target(base)
                .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                .request()
                .post(json(testscenarioObject), TestscenarioDto.class);
        LoggFormater.setCallId();
        logger.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.personopplysninger().søkerIdent());
        return testscenarioDto;
    }

    @Step("Henter alle instansierte testdatascenarier")
    public List<TestscenarioDto> hentAlleScenarier() {
        return client.target(base)
                .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                .request()
                .get(Response.class)
                .readEntity(new GenericType<>() {});
    }
}
