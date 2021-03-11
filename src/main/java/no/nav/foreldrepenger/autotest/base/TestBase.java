package no.nav.foreldrepenger.autotest.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.util.testscenario.TestscenarioHenter;

public abstract class TestBase {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    protected static final TestscenarioHenter TESTSCENARIO_HENTER = new TestscenarioHenter();

}
