package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Hendelse {

    private Kode navn;

    public Hendelse(@JsonProperty("navn") Kode navn) {
        this.navn = navn;
    }

    public Kode getNavn() {
        return navn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hendelse hendelse = (Hendelse) o;
        return Objects.equals(navn, hendelse.navn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(navn);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "<kode" + navn.kode + ", kodeverk=" + navn.kodeverk + ">";
    }
}
