package no.nav.foreldrepenger.autotest.util.testscenario;

import java.util.Collection;

public interface TestscenarioRepository {

    Collection<Object> hentAlleScenarioer();

    Object hentScenario(String scenarioNummer);

}
