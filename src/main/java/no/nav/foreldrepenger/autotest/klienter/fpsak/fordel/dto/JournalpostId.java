package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;


import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class JournalpostId {

    private String journalpostId;

    public JournalpostId(@JsonProperty("journalpostId") String journalpostId) {
        super();
        this.journalpostId = journalpostId;
    }

    public String getJournalpostId() {
        return journalpostId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JournalpostId that = (JournalpostId) o;
        return Objects.equals(journalpostId, that.journalpostId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(journalpostId);
    }

    @Override
    public String toString() {
        return "JournalpostId{" +
                "journalpostId='" + journalpostId + '\'' +
                '}';
    }
}
