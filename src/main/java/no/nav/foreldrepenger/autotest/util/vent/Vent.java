package no.nav.foreldrepenger.autotest.util.vent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Step;

public class Vent {

    protected static final Logger LOG = LoggerFactory.getLogger(Vent.class);

    public static void til(Supplier<Boolean> supplier, int timeoutInSeconds, String failReason) {
        til(supplier, timeoutInSeconds, () -> failReason);
    }

    @Step("Venter til supplier er 'true'; poller i {timeoutInSeconds} sekunder.")
    public static void til(Supplier<Boolean> supplier, int timeoutInSeconds, Supplier<String> errorMessageProducer) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusSeconds(timeoutInSeconds);

        while (!supplier.get()) {
            if (LocalDateTime.now().isAfter(end)) {
                throw new RuntimeException(String.format("Async venting timet ut etter %s sekunder fordi: %s",
                        timeoutInSeconds, errorMessageProducer.get()));
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(
                        String.format("Async venting interrupted ut etter %s sekunder fordi: %s",
                                ChronoUnit.SECONDS.between(start, LocalDateTime.now()), errorMessageProducer.get()),
                        e);
            }
        }
        var between = Duration.between(start, LocalDateTime.now());
        LOG.info("Ventet i {} sekunder", between.getSeconds());
    }
}
