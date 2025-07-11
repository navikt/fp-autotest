package no.nav.foreldrepenger.autotest.util.log;

import java.util.Arrays;

import io.qameta.allure.Allure;

public final class LoggFormater {

    private LoggFormater() {
        // Skal ikke instansieres
    }

    public static String navnPåTestCaseSomKjører() {
        var lifecycle = Allure.getLifecycle();
        final String[] testCaseName = {"Ikke funnet!"};
        if (lifecycle.getCurrentTestCase().isPresent()) {
            lifecycle.updateTestCase(testResult -> testCaseName[0] = formaterTestNavn(testResult));
        }
        return testCaseName[0];
    }

    private static String formaterTestNavn(io.qameta.allure.model.TestResult testResult) {
        var fullName = testResult.getFullName();
        var split = Arrays.asList(fullName.split("\\."));
        return split.get(split.size() - 1);
    }
}
