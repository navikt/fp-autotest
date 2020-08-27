package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class AsyncPollingStatus {

    private Status status;
    private String eta;
    private String message;
    private Integer pollIntervalMillis;
    private String location;
    private String cancelUri;
    private Boolean readOnly;
    private Boolean pending;

    public AsyncPollingStatus(Status status, String eta, String message, Integer pollIntervalMillis, String location,
                              String cancelUri, Boolean readOnly, Boolean pending) {
        this.status = status;
        this.eta = eta;
        this.message = message;
        this.pollIntervalMillis = pollIntervalMillis;
        this.location = location;
        this.cancelUri = cancelUri;
        this.readOnly = readOnly;
        this.pending = pending;
    }

    public Status getStatus() {
        return status;
    }

    public Integer getStatusCode() {
        return status != null ? status.getHttpStatus() : null;
    }

    public String getEta() {
        return eta;
    }

    public String getMessage() {
        return message;
    }

    public Integer getPollIntervalMillis() {
        return pollIntervalMillis;
    }

    public String getLocation() {
        return location;
    }

    public String getCancelUri() {
        return cancelUri;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public boolean isPending() {
        return pending != null ? pending : false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AsyncPollingStatus that = (AsyncPollingStatus) o;
        return status == that.status &&
                Objects.equals(eta, that.eta) &&
                Objects.equals(message, that.message) &&
                Objects.equals(pollIntervalMillis, that.pollIntervalMillis) &&
                Objects.equals(location, that.location) &&
                Objects.equals(cancelUri, that.cancelUri) &&
                Objects.equals(readOnly, that.readOnly) &&
                Objects.equals(pending, that.pending);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, eta, message, pollIntervalMillis, location, cancelUri, readOnly, pending);
    }

    @Override
    public String toString() {
        return "AsyncPollingStatus{" +
                "status=" + status +
                ", eta='" + eta + '\'' +
                ", message='" + message + '\'' +
                ", pollIntervalMillis=" + pollIntervalMillis +
                ", location='" + location + '\'' +
                ", cancelUri='" + cancelUri + '\'' +
                ", readOnly=" + readOnly +
                ", pending=" + pending +
                '}';
    }

    @JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public enum Status {
        PENDING(200),
        COMPLETE(303),
        DELAYED(418),
        CANCELLED(418),
        HALTED(418);

        private Integer httpStatus;

        Status(Integer httpStatus) {
            this.httpStatus = httpStatus;
        }

        public Integer getHttpStatus() {
            return httpStatus;
        }
    }
}
