package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DetaljerteFeilutbetalingsperioderDto {

    protected List<DetaljertFeilutbetalingPeriode> perioder;

    public List<DetaljertFeilutbetalingPeriode> getPerioder() {
        return perioder;
    }
}
