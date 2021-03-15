package no.nav.foreldrepenger.autotest.util.log;

import java.util.Arrays;

import io.qameta.allure.Allure;

public final class LoggFormater {


    public static void setCallId() {
        var lifecycle = Allure.getLifecycle();
        lifecycle.getCurrentTestCase().ifPresentOrElse(t -> lifecycle.updateTestCase(testResult -> {
            MDCOperations.putCallId("Testcase: " + testNavn(testResult));
        }), MDCOperations::removeCallId);
    }

    private static String testNavn(io.qameta.allure.model.TestResult testResult) {
        var fullName = testResult.getFullName();
        var split = Arrays.asList(fullName.split("\\."));
        var size = split.size();
        var testnavn = new StringBuilder();
        for (int i = 0; i < size - 2; i++) {
            testnavn.append(split.get(i).charAt(0));
            testnavn.append(".");
        }
        testnavn.append(split.get(size - 2));
        testnavn.append(".");
        testnavn.append(split.get(size - 1));

        return testnavn.toString();
    }
}
