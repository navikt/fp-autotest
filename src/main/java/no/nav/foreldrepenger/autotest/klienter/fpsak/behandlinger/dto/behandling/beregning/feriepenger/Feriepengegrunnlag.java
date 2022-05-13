package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.feriepenger;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Feriepengegrunnlag {

    protected LocalDate feriepengeperiodeFom;
    protected LocalDate feriepengeperiodeTom;
    protected List<Feriepengeandel> andeler;

    public LocalDate getFeriepengeperiodeFom() {
        return feriepengeperiodeFom;
    }

    public LocalDate getFeriepengeperiodeTom() {
        return feriepengeperiodeTom;
    }

    public List<Feriepengeandel> getAndeler() {
        return andeler;
    }
}
