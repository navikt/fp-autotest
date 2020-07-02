package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BehandlingIdPost {

    private int behandlingId;
    private int behandlingVersjon;

    @JsonCreator
    public BehandlingIdPost(int behandlingId, int behandlingVersjon) {
        super();
        this.behandlingId = behandlingId;
        this.behandlingVersjon = behandlingVersjon;
    }

    public BehandlingIdPost(Behandling behandling) {
        this(behandling.id, behandling.versjon);
    }

    public int getBehandlingId() {
        return behandlingId;
    }

    public int getBehandlingVersjon() {
        return behandlingVersjon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehandlingIdPost that = (BehandlingIdPost) o;
        return behandlingId == that.behandlingId &&
                behandlingVersjon == that.behandlingVersjon;
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, behandlingVersjon);
    }

    @Override
    public String toString() {
        return "BehandlingIdPost{" +
                "behandlingId=" + behandlingId +
                ", behandlingVersjon=" + behandlingVersjon +
                '}';
    }
}
