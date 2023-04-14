package no.nav.foreldrepenger.autotest.util.vent;

import static java.lang.Thread.sleep;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Vent {

    private static final Logger LOG = LoggerFactory.getLogger(Vent.class);

    private Vent() {
    }

    public static void til(Callable<Boolean> callable, int timeoutInSeconds, String failReason) {
        til(callable, timeoutInSeconds, () -> failReason);
    }

    public static void til(Callable<Boolean> callable, int timeoutInSeconds, Callable<String> errorMessageProducer) {
        på(callable, timeoutInSeconds, errorMessageProducer);
    }

    public static <T> T på(Callable<T> callable, int timeoutInSeconds, String failReason) {
        return på(callable, timeoutInSeconds, () -> failReason);
    }

    public static <T> T på(Callable<T> callable, int timeoutInSeconds, Callable<String> errorMessageProducer) {
        var start = LocalDateTime.now();
        var end = start.plusSeconds(timeoutInSeconds);
        var advarsel = start.plusSeconds((int) (timeoutInSeconds * 0.75));
        var logget = false;

        long progressivVentetidMs = 50;

        try {
            T objektReturnert = callable.call();
            while (booleanExpression(objektReturnert)) {
                var now = LocalDateTime.now();
                if (!logget && now.isAfter(advarsel)) {
                    logget = true;
                    var ste = getCallerCallerClassName();
                    LOG.warn("Async venting av {} har tatt mer enn 75% av timeout på {} sekunder!", ste, timeoutInSeconds);
                }
                if (now.isAfter(end)) {
                    throw new IllegalStateException(
                            "Async venting timet ut etter " + timeoutInSeconds + " sekunder fordi: " + errorMessageProducer.call());
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
