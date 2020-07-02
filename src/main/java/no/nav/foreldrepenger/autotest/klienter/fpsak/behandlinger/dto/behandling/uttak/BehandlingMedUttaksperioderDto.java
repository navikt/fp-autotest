package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BehandlingMedUttaksperioderDto {

    private BehandlingIdDto behandlingId;
    private List<UttakResultatPeriode> perioder;

    public BehandlingMedUttaksperioderDto(BehandlingIdDto behandlingId, List<UttakResultatPeriode> perioder) {
        this.behandlingId = behandlingId;
        this.perioder = perioder;
    }

    public BehandlingIdDto getBehandlingId() {
        return behandlingId;
    }

    public void setBehandlingId(BehandlingIdDto behandlingId) {
        this.behandlingId = behandlingId;
    }

    public List<UttakResultatPeriode> getPerioder() {
        return perioder;
    }

    public void setPerioder(List<UttakResultatPeriode> perioder) {
        this.perioder = perioder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BehandlingMedUttaksperioderDto that = (BehandlingMedUttaksperioderDto) o;
        return Objects.equals(behandlingId, that.behandlingId) &&
                Objects.equals(perioder, that.perioder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(behandlingId, perioder);
    }

    @Override
    public String toString() {
        return "BehandlingMedUttaksperioderDto{" +
                "behandlingId=" + behandlingId +
                ", perioder=" + perioder +
                '}';
    }
}
