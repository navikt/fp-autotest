package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.net.URI;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AsyncPollingStatus {

    private final Status status;
    private final LocalDateTime eta;
    private final String message;
    private final Long pollIntervalMillis;
    private URI location;
    private final URI cancelUri;
    private final boolean readOnly;

    @JsonCreator
    public AsyncPollingStatus(@JsonProperty("status") Status status,
                              @JsonProperty("eta") LocalDateTime eta,
                              @JsonProperty("message") String message,
                              @JsonProperty("cancelUri") URI cancelUri,
                              @JsonProperty("pollIntervalMillis") Long pollIntervalMillis) {
        this.status = status;
        this.eta = eta;
        this.message = message;
        this.cancelUri = cancelUri;
        this.pollIntervalMillis = pollIntervalMillis;
        this.readOnly = status == Status.PENDING || status == Status.DELAYED || status == Status.HALTED;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getEta() {
        return eta;
    }

    public String getMessage() {
        return message;
    }

    public URI getCancelUri() {
        return cancelUri;
    }

    public Long getPollIntervalMillis() {
        return pollIntervalMillis;
    }

    public URI getLocation() {
        // kan returneres også i tilfelle feil, for å kunne hente nåværende tilstand, uten hensyn til hva som ikke kan kjøres videre.
        return location;
    }

    public void setLocation(URI uri) {
        this.location = uri;
    }

    public boolean isPending() {
        return Status.PENDING.equals(getStatus());
    }

    public boolean isReadOnly() {
        return readOnly;
    }

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

        public int getHttpStatus() {
            return httpStatus;
        }
    }
}
