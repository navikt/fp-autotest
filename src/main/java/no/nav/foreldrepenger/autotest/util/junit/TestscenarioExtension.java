package no.nav.foreldrepenger.autotest.util.junit;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioJerseyKlient;
import no.nav.foreldrepenger.autotest.util.testscenario.TestscenarioHenter;

/** Denne junit-utvidelsen instansierer TestscenarioHenter og TestscenarioJerseyKlient nøyaktig en gang
 *  for hver tråd som kjøres opp.*/
public class TestscenarioExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

    public static TestscenarioHenter testscenarioHenter;
    public static TestscenarioJerseyKlient testscenarioKlient;

    @Override
    public void beforeAll(ExtensionContext context) {
        var uniqueKey = Thread.currentThread().getName() + "-" + this.getClass().getName();
        var value = context.getRoot().getStore(GLOBAL).get(uniqueKey);
        if (value == null) {
            context.getRoot().getStore(GLOBAL).put(uniqueKey, this);
            initialiser();
        }
    }

    private void initialiser() {
        testscenarioHenter = new TestscenarioHenter();
        testscenarioKlient = new TestscenarioJerseyKlient();
    }

    @Override
    public void close() {
    }
}
