package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeilutbetalingFakta {

    protected List<FeilutbetalingPerioder> perioder;

    public List<FeilutbetalingPerioder> getPerioder() {
        return perioder;
    }
}
