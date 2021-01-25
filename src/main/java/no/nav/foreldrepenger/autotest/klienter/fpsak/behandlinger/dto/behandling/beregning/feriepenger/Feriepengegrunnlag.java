package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.feriepenger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Feriepengegrunnlag {
    protected LocalDate feriepengeperiodeFom;
    protected LocalDate feriepengeperiodeTom;
    protected List<FeriepengegrunnlagAndel> andeler;

    public Feriepengegrunnlag() {
    }

    public LocalDate getFeriepengeperiodeFom() {
        return feriepengeperiodeFom;
    }

    public LocalDate getFeriepengeperiodeTom() {
        return feriepengeperiodeTom;
    }

    public List<FeriepengegrunnlagAndel> getAndeler() {
        return andeler;
    }
}
