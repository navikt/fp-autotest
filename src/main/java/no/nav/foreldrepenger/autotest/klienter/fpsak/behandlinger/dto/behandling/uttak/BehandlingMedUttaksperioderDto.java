package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BehandlingMedUttaksperioderDto {

    protected UUID behandlingUuid;
    protected List<UttakResultatPeriode> perioder;

    public UUID getBehandlingUuid() {
        return behandlingUuid;
    }

    public void setBehandlingUuid(UUID behandlingUuid) {
        this.behandlingUuid = behandlingUuid;
    }

    public List<UttakResultatPeriode> getPerioder() {
        return perioder;
    }

    public void setPerioder(List<UttakResultatPeriode> perioder) {
        this.perioder = perioder;
    }

}
