package no.nav.foreldrepenger.autotest.util.log;

import java.util.Arrays;

import io.qameta.allure.Allure;
import no.nav.vedtak.log.mdc.MDCOperations;

public final class LoggFormater {

    private LoggFormater() {
        // Skal ikke instansieres
    }

    public static void leggTilKjørendeTestCaseILogger() {
        var lifecycle = Allure.getLifecycle();
        lifecycle.getCurrentTestCase().ifPresentOrElse(
                t -> lifecycle.updateTestCase(testResult -> MDCOperations.putCallId(testNavn(testResult))),
                MDCOperations::removeCallId);
    }

    private static String testNavn(io.qameta.allure.model.TestResult testResult) {
        var fullName = testResult.getFullName();
        var split = Arrays.asList(fullName.split("\\."));
        return split.get(split.size() - 1);
    }
}
