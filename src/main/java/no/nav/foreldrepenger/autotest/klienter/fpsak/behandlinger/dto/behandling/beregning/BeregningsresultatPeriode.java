package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeregningsresultatPeriode {

    protected LocalDate fom;
    protected LocalDate tom;
    protected int dagsats;
    protected List<BeregningsresultatPeriodeAndel> andeler;

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public int getDagsats() {
        return dagsats;
    }

    public List<BeregningsresultatPeriodeAndel> getAndeler() {
        return andeler;
    }

}
