package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BehandlingNy {
    private Long saksnummer;
    private String behandlingType;
    private String behandlingArsakType;
    private Boolean nyBehandlingEtterKlage;

    public BehandlingNy(Long saksnummer, String behandlingType, String behandlingArsakType, Boolean nyBehandlingEtterKlage) {
        this.saksnummer = saksnummer;
        this.behandlingType = behandlingType;
        this.behandlingArsakType = behandlingArsakType;
        this.nyBehandlingEtterKlage = nyBehandlingEtterKlage;
    }

    public Long getSaksnummer() {
        return saksnummer;
    }

    public String getBehandlingType() {
        return behandlingType;
    }

    public String getBehandlingArsakType() {
        return behandlingArsakType;
    }

    public Boolean getNyBehandlingEtterKlage() {
        return nyBehandlingEtterKlage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehandlingNy that = (BehandlingNy) o;
        return Objects.equals(saksnummer, that.saksnummer) &&
                Objects.equals(behandlingType, that.behandlingType) &&
                Objects.equals(behandlingArsakType, that.behandlingArsakType) &&
                Objects.equals(nyBehandlingEtterKlage, that.nyBehandlingEtterKlage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(saksnummer, behandlingType, behandlingArsakType, nyBehandlingEtterKlage);
    }

    @Override
    public String toString() {
        return "BehandlingNy{" +
                "saksnummer=" + saksnummer +
                ", behandlingType='" + behandlingType + '\'' +
                ", behandlingArsakType='" + behandlingArsakType + '\'' +
                ", nyBehandlingEtterKlage=" + nyBehandlingEtterKlage +
                '}';
    }
}
