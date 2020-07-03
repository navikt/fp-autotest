package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public class TestscenarioKlient extends VTPKlient {

    private static final Logger logger = LoggerFactory.getLogger(TestscenarioKlient.class);

    private static final String TESTSCENARIO_I_AUTOTEST_POST_URL = "/testscenarios";

    public TestscenarioKlient(HttpSession session) {
        super(session);
    }

    public TestscenarioDto opprettTestscenarioMedAktorId(String key, Object testscenarioObject, String aktorId) {
        String url = hentRestRotUrl() + TESTSCENARIO_I_AUTOTEST_POST_URL + "?aktor1=" + aktorId;
        TestscenarioDto testscenarioDto = postOgHentJson(url, testscenarioObject, TestscenarioDto.class, StatusRange.STATUS_SUCCESS);
        logger.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.getPersonopplysninger().getSøkerIdent());
        return testscenarioDto;
    }

    public TestscenarioDto opprettTestscenario(String key, Object testscenarioObject) {
        String url = hentRestRotUrl() + TESTSCENARIO_I_AUTOTEST_POST_URL;
        TestscenarioDto testscenarioDto = postOgHentJson(url, testscenarioObject, TestscenarioDto.class, StatusRange.STATUS_SUCCESS);
        logger.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.getPersonopplysninger().getSøkerIdent());
        return testscenarioDto;
    }
}
