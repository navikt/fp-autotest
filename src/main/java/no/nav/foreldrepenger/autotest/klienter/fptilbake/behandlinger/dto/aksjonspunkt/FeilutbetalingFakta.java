package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeilutbetalingFakta {

    private int tidligereVarsletBeløp;
    private int aktuellFeilUtbetaltBeløp;

    protected List<FeilutbetalingPerioder> perioder;

    public List<FeilutbetalingPerioder> getPerioder() {
        return perioder;
    }
}
