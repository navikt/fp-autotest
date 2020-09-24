package no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Fagsak {

    private long saksnummer;
    private Kode status;

    public Fagsak(long saksnummer, Kode status) {
        this.saksnummer = saksnummer;
        this.status = status;
    }

    public long getSaksnummer() {
        return saksnummer;
    }

    public Kode getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fagsak fagsak = (Fagsak) o;
        return saksnummer == fagsak.saksnummer &&
                Objects.equals(status, fagsak.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saksnummer, status);
    }

    @Override
    public String toString() {
        return "Fagsak{" +
                "saksnummer=" + saksnummer +
                ", status=" + status +
                '}';
    }
}
