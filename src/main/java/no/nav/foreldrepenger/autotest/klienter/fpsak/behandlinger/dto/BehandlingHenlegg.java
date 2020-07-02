package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingHenlegg extends BehandlingIdPost {

    private String årsakKode;
    private String begrunnelse;

    public BehandlingHenlegg(int behandlingId, int behandlingVersjon, String årsakKode, String begrunnelse) {
        super(behandlingId, behandlingVersjon);
        this.årsakKode = årsakKode;
        this.begrunnelse = begrunnelse;
    }

    public String getÅrsakKode() {
        return årsakKode;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BehandlingHenlegg that = (BehandlingHenlegg) o;
        return Objects.equals(årsakKode, that.årsakKode) &&
                Objects.equals(begrunnelse, that.begrunnelse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), årsakKode, begrunnelse);
    }

    @Override
    public String toString() {
        return "BehandlingHenlegg{" +
                "årsakKode='" + årsakKode + '\'' +
                ", begrunnelse='" + begrunnelse + '\'' +
                "} " + super.toString();
    }
}
