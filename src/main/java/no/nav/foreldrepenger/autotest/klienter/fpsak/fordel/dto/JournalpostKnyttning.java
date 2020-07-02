package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class JournalpostKnyttning {

    private Saksnummer saksnummerDto;
    private JournalpostId journalpostIdDto;

    public JournalpostKnyttning(Saksnummer saksnummerDto, JournalpostId journalpostId) {
        super();
        this.saksnummerDto = saksnummerDto;
        this.journalpostIdDto = journalpostId;
    }

    public Saksnummer getSaksnummerDto() {
        return saksnummerDto;
    }

    public JournalpostId getJournalpostIdDto() {
        return journalpostIdDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JournalpostKnyttning that = (JournalpostKnyttning) o;
        return Objects.equals(saksnummerDto, that.saksnummerDto) &&
                Objects.equals(journalpostIdDto, that.journalpostIdDto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saksnummerDto, journalpostIdDto);
    }

    @Override
    public String toString() {
        return "JournalpostKnyttning{" +
                "saksnummerDto=" + saksnummerDto +
                ", journalpostIdDto=" + journalpostIdDto +
                '}';
    }
}
