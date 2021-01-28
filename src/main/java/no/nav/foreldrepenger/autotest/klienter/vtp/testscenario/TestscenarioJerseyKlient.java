package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import static javax.ws.rs.client.Entity.json;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPJerseyKlient;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public class TestscenarioJerseyKlient extends VTPJerseyKlient {

    private static final Logger logger = LoggerFactory.getLogger(TestscenarioJerseyKlient.class);

    private static final String TESTSCENARIO_I_AUTOTEST_POST_URL = "/testscenarios";

    public TestscenarioJerseyKlient() {
        super();
    }

    public TestscenarioDto opprettTestscenarioMedAktorId(String key, Object testscenarioObject, String aktorId, String ident) {
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

    public TestscenarioDto opprettTestscenario(String key, Object testscenarioObject) {
        var testscenarioDto = client.target(base)
                .path(TESTSCENARIO_I_AUTOTEST_POST_URL)
                .request()
                .post(json(testscenarioObject), TestscenarioDto.class);
        logger.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.personopplysninger().søkerIdent());
        return testscenarioDto;
    }
}