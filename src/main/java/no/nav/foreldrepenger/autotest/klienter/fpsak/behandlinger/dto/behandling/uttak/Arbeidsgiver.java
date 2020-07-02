package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Arbeidsgiver implements Serializable {

    private String identifikator;
    private String navn;
    private String aktørId;
    private Boolean virksomhet;

    public Arbeidsgiver(String identifikator, String navn, String aktørId, Boolean virksomhet) {
        this.identifikator = identifikator;
        this.navn = navn;
        this.aktørId = aktørId;
        this.virksomhet = virksomhet;
    }

    public String getIdentifikator() {
        return identifikator;
    }

    public String getNavn() {
        return navn;
    }

    public String getAktørId() {
        return aktørId;
    }

    public Boolean getVirksomhet() {
        return virksomhet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arbeidsgiver that = (Arbeidsgiver) o;
        return Objects.equals(identifikator, that.identifikator) &&
                Objects.equals(navn, that.navn) &&
                Objects.equals(aktørId, that.aktørId) &&
                Objects.equals(virksomhet, that.virksomhet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifikator, navn, aktørId, virksomhet);
    }

    @Override
    public String toString() {
        return "Arbeidsgiver{" +
                "identifikator='" + identifikator + '\'' +
                ", navn='" + navn + '\'' +
                ", aktørId='" + aktørId + '\'' +
                ", virksomhet=" + virksomhet +
                '}';
    }
}
