package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Saldoer {

    protected LocalDate maksDatoUttak;

    protected Map<Stønadskonto, Stonadskontoer> stonadskontoer;

    public LocalDate getMaksDatoUttak() {
        return this.maksDatoUttak;
    }

    public Map<Stønadskonto, Stonadskontoer> getStonadskontoer() {
        return this.stonadskontoer;
    }
}
