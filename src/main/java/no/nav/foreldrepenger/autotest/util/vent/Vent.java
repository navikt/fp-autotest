package no.nav.foreldrepenger.autotest.util.vent;

import static java.lang.Thread.sleep;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Vent {

    private static final Logger LOG = LoggerFactory.getLogger(Vent.class);
    private static final int TIMEOUT_SEKUNDER = 15;

    private Vent() {
    }

    public static void til(Callable<Boolean> callable, String failReason) {
        til(callable, () -> failReason);
    }

    public static void til(Callable<Boolean> callable, Callable<String> errorMessageProducer) {
        på(callable, errorMessageProducer);
    }

    public static <T> T på(Callable<T> callable, String failReason) {
        return på(callable, () -> failReason);
    }

    public static <T> T på(Callable<T> callable, Callable<String> errorMessageProducer) {
        return på(callable, errorMessageProducer, 50);
    }

    public static <T> T på(Callable<T> callable, Callable<String> errorMessageProducer, int progressivVentetid) {
        var start = LocalDateTime.now();
        var end = start.plusSeconds(TIMEOUT_SEKUNDER);
        var advarsel = start.plusSeconds((int) (TIMEOUT_SEKUNDER * 0.75));
        var logget = false;

        long progressivVentetidMs = progressivVentetid;

        try {
            T objektReturnert = callable.call();
            while (booleanExpression(objektReturnert)) {
                var now = LocalDateTime.now();
                if (!logget && now.isAfter(advarsel)) {
                    logget = true;
                    var ste = getCallerCallerClassName();
                    LOG.warn("Async venting av {} har tatt mer enn 75% av timeout på {} sekunder!", ste, TIMEOUT_SEKUNDER);
                }
                if (now.isAfter(end)) {
                    throw new IllegalStateException(
                            "Async venting timet ut etter " + TIMEOUT_SEKUNDER + " sekunder fordi: " + errorMessageProducer.call());
                }
                sleep(progressivVentetidMs);
                progressivVentetidMs = Math.min(750, 2 * progressivVentetidMs);
                objektReturnert = callable.call();
            }
            return objektReturnert;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static <T> boolean booleanExpression(T callable) {
        if (callable instanceof Boolean) {
            return Boolean.FALSE.equals(callable);
        }
        return callable == null;
    }


    private static StackTraceElement getCallerCallerClassName() {
        var stElements = Thread.currentThread().getStackTrace();
        for (var i = 1; i < stElements.length; i++) {
            var ste = stElements[i];
            if (!ste.getClassName().equals(Vent.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return ste;
            }
        }
        return null;
    }
}
