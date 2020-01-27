package no.nav.foreldrepenger.autotest.util.vent;

import io.qameta.allure.Step;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

public class Vent {

    public static void til(Callable<Boolean> callable, int timeoutInSeconds, String failReason) throws Exception {
        til(callable, timeoutInSeconds, new Callable<String>() {

            @Override
            public String call() throws Exception {
                return failReason;
            }
        });
    }

    @Step("Venter til callable er 'true'; poller i {timeoutInSeconds} sekunder.")
    public static void til(Callable<Boolean> callable, int timeoutInSeconds, Callable<String> errorMessageProducer) throws Exception {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusSeconds(timeoutInSeconds);

        while (!callable.call()) {
            if (LocalDateTime.now().isAfter(end)) {
                throw new RuntimeException(String.format("Async venting timet ut etter %s sekunder fordi: %s", timeoutInSeconds, errorMessageProducer.call()));
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(
                    String.format("Async venting interrupted ut etter %s sekunder fordi: %s", ChronoUnit.SECONDS.between(start, LocalDateTime.now()), errorMessageProducer.call()), e);
            }
        }
    }
}
