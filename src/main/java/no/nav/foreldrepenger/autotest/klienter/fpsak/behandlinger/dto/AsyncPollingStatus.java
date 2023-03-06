package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.net.URI;
import java.time.LocalDateTime;

public record AsyncPollingStatus(Status status,
                                 LocalDateTime eta,
                                 String message,
                                 Long pollIntervalMillis,
                                 URI location,
                                 URI cancelUri,
                                 boolean readOnly) {

    public enum Status {
        PENDING(200),
        COMPLETE(303),
        DELAYED(418),
        CANCELLED(418),
        HALTED(418);

        private final int httpStatus;

        Status(int httpStatus){
            this.httpStatus = httpStatus;
        }

        public int httpStatus() {
            return httpStatus;
        }
    }
}
