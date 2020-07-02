package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.BehandlingIdDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingMedUttaksperioderDto {

    protected BehandlingIdDto behandlingId;
    protected List<UttakResultatPeriode> perioder;

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

}
