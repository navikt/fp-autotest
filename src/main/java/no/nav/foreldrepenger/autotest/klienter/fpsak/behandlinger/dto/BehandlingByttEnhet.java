package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BehandlingByttEnhet extends BehandlingIdPost {

    private String enhetNavn;
    private String enhetId;
    private String begrunnelse;

    public BehandlingByttEnhet(int behandlingId, int behandlingVersjon, String enhetNavn, String enhetId,
            String begrunnelse) {
        super(behandlingId, behandlingVersjon);
        this.enhetNavn = enhetNavn;
        this.enhetId = enhetId;
        this.begrunnelse = begrunnelse;
    }

    public String getEnhetNavn() {
        return enhetNavn;
    }

    public String getEnhetId() {
        return enhetId;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BehandlingByttEnhet that = (BehandlingByttEnhet) o;
        return Objects.equals(enhetNavn, that.enhetNavn) &&
                Objects.equals(enhetId, that.enhetId) &&
                Objects.equals(begrunnelse, that.begrunnelse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), enhetNavn, enhetId, begrunnelse);
    }

    @Override
    public String toString() {
        return "BehandlingByttEnhet{" +
                "enhetNavn='" + enhetNavn + '\'' +
                ", enhetId='" + enhetId + '\'' +
                ", begrunnelse='" + begrunnelse + '\'' +
                "} " + super.toString();
    }
}
