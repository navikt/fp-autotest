package no.nav.foreldrepenger.autotest.klienter.fpsak.fordel.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FagsakInformasjon {

    private String aktørId;
    private String behandlingstemaOffisiellKode;

    public FagsakInformasjon(String aktørId, String behandlingstemaOffisiellKode) {
        this.aktørId = aktørId;
        this.behandlingstemaOffisiellKode = behandlingstemaOffisiellKode;
    }

    public String getAktørId() {
        return aktørId;
    }

    public String getBehandlingstemaOffisiellKode() {
        return behandlingstemaOffisiellKode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FagsakInformasjon that = (FagsakInformasjon) o;
        return Objects.equals(aktørId, that.aktørId) &&
                Objects.equals(behandlingstemaOffisiellKode, that.behandlingstemaOffisiellKode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aktørId, behandlingstemaOffisiellKode);
    }

    @Override
    public String toString() {
        return "FagsakInformasjon{" +
                "aktørId='" + aktørId + '\'' +
                ", behandlingstemaOffisiellKode='" + behandlingstemaOffisiellKode + '\'' +
                '}';
    }
}
