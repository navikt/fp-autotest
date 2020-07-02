package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UidentifisertBarn {
    private LocalDate fodselsdato;
    private LocalDate dodsdato;

    UidentifisertBarn() {
    }

    @JsonCreator
    public UidentifisertBarn(LocalDate fodselsdato, LocalDate dodsdato) {
        this.fodselsdato = fodselsdato;
        this.dodsdato = dodsdato;
    }

    public LocalDate getFodselsdato() {
        return fodselsdato;
    }

    public LocalDate getDodsdato() {
        return dodsdato;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UidentifisertBarn that = (UidentifisertBarn) o;
        return Objects.equals(fodselsdato, that.fodselsdato) &&
                Objects.equals(dodsdato, that.dodsdato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fodselsdato, dodsdato);
    }

    @Override
    public String toString() {
        return "UidentifisertBarn{" +
                "fodselsdato=" + fodselsdato +
                ", dodsdato=" + dodsdato +
                '}';
    }
}
