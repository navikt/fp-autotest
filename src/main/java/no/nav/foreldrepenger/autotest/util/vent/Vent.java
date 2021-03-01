package no.nav.foreldrepenger.autotest.util.vent;

import java.time.LocalDateTime;
import java.util.concurrent.Callable;

public final class Vent {

    private Vent() {
    }

    public static void til(Callable<Boolean> callable, int timeoutInSeconds, String failReason) {
        til(callable, timeoutInSeconds, () -> failReason);
    }

    public static void til(Callable<Boolean> callable, int timeoutInSeconds, Callable<String> errorMessageProducer) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusSeconds(timeoutInSeconds);

        try {
            while (Boolean.FALSE.equals(callable.call())) {
                if (LocalDateTime.now().isAfter(end)) {
                    throw new RuntimeException(
                            String.format("Async venting timet ut etter %s sekunder fordi: %s", timeoutInSeconds,
                                    errorMessageProducer.call()));
                }
                Thread.sleep(1000);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
