package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Saldoer {

    private final LocalDate maksDatoUttak;

    private final Map<StønadskontoType, Stonadskontoer> stonadskontoer;

    // TODO: Her mangler StønadskontoType FLERBARNSDAGER som er i fpsak, men ikke i mottak. Egen modell for fpsak??
    public Saldoer(LocalDate maksDatoUttak, Map<StønadskontoType, Stonadskontoer> stonadskontoer) {
        this.maksDatoUttak = maksDatoUttak;
        this.stonadskontoer = stonadskontoer;
    }

    public LocalDate getMaksDatoUttak() {
        return this.maksDatoUttak;
    }

    public Map<StønadskontoType, Stonadskontoer> getStonadskontoer() {
        return this.stonadskontoer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maksDatoUttak, stonadskontoer);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Saldoer other = (Saldoer) obj;
        return Objects.equals(this.stonadskontoer, other.stonadskontoer)
                && Objects.equals(this.maksDatoUttak, other.maksDatoUttak);

    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [maksDatoUttak=" + maksDatoUttak + ", stonadskontoer=" + stonadskontoer
                + "]";
    }
}
