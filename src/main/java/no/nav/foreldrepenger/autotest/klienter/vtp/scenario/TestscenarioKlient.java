package no.nav.foreldrepenger.autotest.klienter.vtp.scenario;

import java.io.IOException;

import no.nav.foreldrepenger.autotest.klienter.vtp.VTPKlient;
import no.nav.foreldrepenger.autotest.util.http.HttpSession;
import no.nav.foreldrepenger.autotest.util.http.rest.StatusRange;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public class TestscenarioKlient extends VTPKlient{

    private static final String TESTSCENARIO_POST_URL = "/testscenario/%s";

    public TestscenarioKlient(HttpSession session) {
        super(session);
    }


    public TestscenarioDto opprettTestscenario(String key) throws IOException {
        String url = hentRestRotUrl() + String.format(TESTSCENARIO_POST_URL, key);
        TestscenarioDto testscenarioDto = postOgHentJson(url, null, TestscenarioDto.class, StatusRange.STATUS_SUCCESS);
        System.out.println("Testscenario opprettet: [" + key + "] med hovedsøker: [" + testscenarioDto.getPersonopplysninger().getSøkerIdent() + "]");
        return testscenarioDto;
    }

    public TestscenarioDto opprettTestscenarioMedAktorId(String key, String aktorId) throws IOException {
        String url = hentRestRotUrl() + String.format(TESTSCENARIO_POST_URL, key) + "?aktor1=" + aktorId;
        TestscenarioDto testscenarioDto = postOgHentJson(url, null, TestscenarioDto.class, StatusRange.STATUS_SUCCESS);
        System.out.println("Testscenario opprettet: [" + key + "] med hovedsøker: [" + testscenarioDto.getPersonopplysninger().getSøkerIdent() + "]");
        return testscenarioDto;
    }
}
