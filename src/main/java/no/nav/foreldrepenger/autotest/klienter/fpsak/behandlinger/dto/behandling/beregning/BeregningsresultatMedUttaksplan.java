package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeregningsresultatMedUttaksplan {

    protected boolean sokerErMor;
    protected LocalDate opphoersdato;
    protected List<BeregningsresultatPeriode> perioder;

    public boolean isSokerErMor() {
        return sokerErMor;
    }

    public LocalDate getOpphoersdato() {
        return opphoersdato;
    }

    public List<BeregningsresultatPeriode> getPerioder() {
        return perioder;
    }

}
