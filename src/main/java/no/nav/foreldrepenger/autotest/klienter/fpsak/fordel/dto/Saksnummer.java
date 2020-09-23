package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Saksnummer {

    private long saksnummer;

    public Saksnummer(@JsonProperty("saksnummer") Long saksnummer) {
        this.saksnummer = saksnummer;
    }

    public long getSaksnummer() {
        return saksnummer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Saksnummer that = (Saksnummer) o;
        return saksnummer == that.saksnummer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(saksnummer);
    }

    @Override
    public String toString() {
        return "Saksnummer{" +
                "saksnummer=" + saksnummer +
                '}';
    }
}
