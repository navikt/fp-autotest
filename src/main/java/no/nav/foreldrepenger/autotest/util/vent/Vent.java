package no.nav.foreldrepenger.autotest.util.vent;

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
        var start = LocalDateTime.now();
        var end = start.plusSeconds(timeoutInSeconds);
        var advarsel = start.plusSeconds((int) (timeoutInSeconds * 0.75));
        var logget = false;

        try {
            while (Boolean.FALSE.equals(callable.call())) {
                var now = LocalDateTime.now();
                if (!logget && now.isAfter(advarsel)) {
                    logget = true;
                    var ste = getCallerCallerClassName();
                    LOG.warn("Async venting av {} har tatt mer enn 75% av timeout på {} sekunder!", ste, timeoutInSeconds);
                }
                if (now.isAfter(end)) {
                    throw new IllegalStateException(
                            String.format("Async venting timet ut etter %s sekunder fordi: %s", timeoutInSeconds, errorMessageProducer.call()));
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    private static StackTraceElement getCallerCallerClassName() {
        var stElements = Thread.currentThread().getStackTrace();
        for (var i=1; i<stElements.length; i++) {
            var ste = stElements[i];
            if (!ste.getClassName().equals(Vent.class.getName())&& ste.getClassName().indexOf("java.lang.Thread")!= 0) {
                return ste;
            }
        }
        return null;
    }
}
