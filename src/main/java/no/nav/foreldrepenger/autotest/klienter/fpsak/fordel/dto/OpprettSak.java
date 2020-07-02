package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class OpprettSak {

    private String journalpostId;
    private String behandlingstemaOffisiellKode;
    private String aktørId;

    public OpprettSak(String journalpostId, String behandlingstemaOffisiellKode, String aktørId) {
        this.journalpostId = journalpostId;
        this.behandlingstemaOffisiellKode = behandlingstemaOffisiellKode;
        this.aktørId = aktørId;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    public String getBehandlingstemaOffisiellKode() {
        return behandlingstemaOffisiellKode;
    }

    public String getAktørId() {
        return aktørId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpprettSak that = (OpprettSak) o;
        return Objects.equals(journalpostId, that.journalpostId) &&
                Objects.equals(behandlingstemaOffisiellKode, that.behandlingstemaOffisiellKode) &&
                Objects.equals(aktørId, that.aktørId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalpostId, behandlingstemaOffisiellKode, aktørId);
    }

    @Override
    public String toString() {
        return "OpprettSak{" +
                "journalpostId='" + journalpostId + '\'' +
                ", behandlingstemaOffisiellKode='" + behandlingstemaOffisiellKode + '\'' +
                ", aktørId='" + aktørId + '\'' +
                '}';
    }
}
