package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

import java.io.IOException;

public class TestscenarioFraAutotestKlient extends VTPKlient {

    private static final String TESTSCENARIO_FRA_AUTOTEST_POST_URL = "/testscenario/test/initialiser";

    public TestscenarioFraAutotestKlient(HttpSession session){
        super(session);
    }

    public TestscenarioDto initialiserTestscenario(String key, Object testscenarioObject) throws IOException {
        String url = hentRestRotUrl() + TESTSCENARIO_FRA_AUTOTEST_POST_URL;
        TestscenarioDto testscenarioDto = postOgHentJson(url, testscenarioObject, TestscenarioDto.class, StatusRange.STATUS_SUCCESS);
        System.out.println("Testscenario initialisert: [" + key + "] med hovedsøker: [" + testscenarioDto.getPersonopplysninger().getSøkerIdent() + "]");
        return testscenarioDto;
    }

}
