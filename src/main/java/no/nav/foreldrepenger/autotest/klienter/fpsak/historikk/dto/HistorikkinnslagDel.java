package no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class HistorikkinnslagDel {

    private Hendelse hendelse;

    public HistorikkinnslagDel(@JsonProperty("hendelse") Hendelse hendelse) {
        this.hendelse = hendelse;
    }

    public Hendelse getHendelse() {
        return hendelse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistorikkinnslagDel that = (HistorikkinnslagDel) o;
        return Objects.equals(hendelse, that.hendelse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hendelse);
    }

    @Override
    public String toString() {
        return String.valueOf(hendelse);
    }
}
