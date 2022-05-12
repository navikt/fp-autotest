package no.nav.foreldrepenger.autotest.util.log;

import static no.nav.vedtak.log.mdc.MDCOperations.MDC_CONSUMER_ID;

import java.util.Arrays;
import java.util.UUID;

import io.qameta.allure.Allure;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.vedtak.log.mdc.MDCOperations;

public final class LoggFormater {

    private LoggFormater() {
        // Skal ikke instansieres
    }

    public static void leggTilKjørendeTestCaseILogger() {
        var lifecycle = Allure.getLifecycle();
        lifecycle.getCurrentTestCase().ifPresentOrElse(t -> lifecycle.updateTestCase(testResult ->
                MDCOperations.putCallId(testNavn(testResult))), MDCOperations::removeCallId);
    }

    private static String testNavn(io.qameta.allure.model.TestResult testResult) {
        var fullName = testResult.getFullName();
        var split = Arrays.asList(fullName.split("\\."));
        var size = split.size();
        var testnavn = new StringBuilder();
        for (var i = 0; i < size - 2; i++) {
            testnavn.append(split.get(i).charAt(0));
            testnavn.append(".");
        }
        testnavn.append(split.get(size - 2));
        testnavn.append(".");
        testnavn.append(split.get(size - 1));

        return testnavn.toString();
    }

    public static void leggTilCallIdforSaksnummerForLogging(Fødselsnummer fnr, Long saksnummer) {
        // Legger til Callid for saksnummer slik at vi kan slå opp riktig callid senere
        var saksnr = String.valueOf(saksnummer);
        MDCOperations.putToMDC(saksnr, leggTilCallIdForFnr(fnr) + "_" + saksnr);
    }

    public static String leggTilCallIdForFnr(Fødselsnummer fnr) {
        var callId = MDCOperations.getFromMDC(fnr.value());
        if (callId == null || callId.isEmpty()) {
            callId = MDCOperations.generateCallId();
            MDCOperations.putToMDC(fnr.value(), callId);
            MDCOperations.putToMDC(MDC_CONSUMER_ID, callId);
        }
        return callId;
    }
}
