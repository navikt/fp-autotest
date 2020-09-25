package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Status {
    private String status;
    private String message;

    public Status(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status1 = (Status) o;
        return Objects.equals(status, status1.status) &&
                Objects.equals(message, status1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message);
    }

    @Override
    public String toString() {
        return "Status{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
