package no.nav.foreldrepenger.autotest.klienter.vtp.testscenario;

import java.io.IOException;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public class TestscenarioKlient extends VTPKlient{

    private static final String TESTSCENARIO_I_AUTOTEST_POST_URL = "/testscenario";
    private static final String TESTSCENARIO_I_VTP_POST_URL = "/testscenario/%s";

    public TestscenarioKlient(HttpSession session) {
        super(session);
    }

    @Deprecated
    public TestscenarioDto opprettTestscenarioFraVTPTemplate(String key) throws IOException {
        String url = hentRestRotUrl() + String.format(TESTSCENARIO_I_VTP_POST_URL, key);
        TestscenarioDto testscenarioDto = postOgHentJson(url, null, TestscenarioDto.class, StatusRange.STATUS_SUCCESS);
        System.out.println("Testscenario opprettet: [" + key + "] med hovedsøker: [" + testscenarioDto.getPersonopplysninger().getSøkerIdent() + "]");
        return testscenarioDto;
    }
    @Deprecated
    public TestscenarioDto opprettTestscenarioMedAktorIdFraVTPTemplate(String key, String aktorId) throws IOException {
        String url = hentRestRotUrl() + String.format(TESTSCENARIO_I_VTP_POST_URL, key) + "?aktor1=" + aktorId;
        TestscenarioDto testscenarioDto = postOgHentJson(url, null, TestscenarioDto.class, StatusRange.STATUS_SUCCESS);
        System.out.println("Testscenario opprettet: [" + key + "] med hovedsøker: [" + testscenarioDto.getPersonopplysninger().getSøkerIdent() + "]");
        return testscenarioDto;
    }

    public TestscenarioDto opprettTestscenario(String key, Object testscenarioObject) throws IOException {
        String url = hentRestRotUrl() + TESTSCENARIO_I_AUTOTEST_POST_URL;
        TestscenarioDto testscenarioDto = postOgHentJson(url, testscenarioObject, TestscenarioDto.class, StatusRange.STATUS_SUCCESS);
        System.out.println("Testscenario initialisert: [" + key + "] med hovedsøker: [" + testscenarioDto.getPersonopplysninger().getSøkerIdent() + "]");
        return testscenarioDto;
    }
}
