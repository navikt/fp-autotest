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
    private static final String TESTSCENARIO_I_VTP_POST_URL = "/testscenarios/%s";

    public TestscenarioKlient(HttpSession session) {
        super(session);
    }

    @Deprecated
    public TestscenarioDto opprettTestscenarioFraVTPTemplate(String key) {
        String url = hentRestRotUrl() + String.format(TESTSCENARIO_I_VTP_POST_URL, key);
        TestscenarioDto testscenarioDto = postOgHentJson(url, null, TestscenarioDto.class, StatusRange.STATUS_SUCCESS);
        logger.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.getPersonopplysninger().getSøkerIdent());
        return testscenarioDto;
    }

    @Deprecated
    public TestscenarioDto opprettTestscenarioMedAktorIdFraVTPTemplate(String key, String aktorId) {
        String url = hentRestRotUrl() + String.format(TESTSCENARIO_I_VTP_POST_URL, key) + "?aktor1=" + aktorId;
        TestscenarioDto testscenarioDto = postOgHentJson(url, null, TestscenarioDto.class, StatusRange.STATUS_SUCCESS);
        logger.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.getPersonopplysninger().getSøkerIdent());
        return testscenarioDto;
    }

    public TestscenarioDto opprettTestscenario(String key, Object testscenarioObject) {
        String url = hentRestRotUrl() + TESTSCENARIO_I_AUTOTEST_POST_URL;
        TestscenarioDto testscenarioDto = postOgHentJson(url, testscenarioObject, TestscenarioDto.class,
                StatusRange.STATUS_SUCCESS);
        logger.info("Testscenario opprettet: [{}] med hovedsøker: [{}]", key,
                testscenarioDto.getPersonopplysninger().getSøkerIdent());
        return testscenarioDto;
    }
}
