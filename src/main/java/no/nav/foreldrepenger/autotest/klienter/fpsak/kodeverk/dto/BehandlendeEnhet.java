package no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BehandlendeEnhet {

    private String enhetId;
    private String enhetNavn;
    private String status;

    public BehandlendeEnhet(String enhetId, String enhetNavn, String status) {
        this.enhetId = enhetId;
        this.enhetNavn = enhetNavn;
        this.status = status;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public String getEnhetNavn() {
        return enhetNavn;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehandlendeEnhet that = (BehandlendeEnhet) o;
        return Objects.equals(enhetId, that.enhetId) &&
                Objects.equals(enhetNavn, that.enhetNavn) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enhetId, enhetNavn, status);
    }

    @Override
    public String toString() {
        return "BehandlendeEnhet{" +
                "enhetId='" + enhetId + '\'' +
                ", enhetNavn='" + enhetNavn + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
