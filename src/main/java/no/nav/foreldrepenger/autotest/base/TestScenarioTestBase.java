package no.nav.foreldrepenger.autotest.base;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioJerseyKlient;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

public abstract class TestScenarioTestBase {

    private static final TestscenarioJerseyKlient testscenarioKlient = new TestscenarioJerseyKlient();

    protected TestscenarioDto opprettTestscenarioMedPrivatArbeidsgiver(String id, String aktorId, String ident) {
        return testscenarioKlient.opprettTestscenarioMedAktorId(id, aktorId, ident);
    }

    protected TestscenarioDto opprettTestscenario(String id) {
        return testscenarioKlient.opprettTestscenario(id);
    }

    protected List<TestscenarioDto> hentTestscenario() {
        return testscenarioKlient.hentAlleScenarier();
    }
}
