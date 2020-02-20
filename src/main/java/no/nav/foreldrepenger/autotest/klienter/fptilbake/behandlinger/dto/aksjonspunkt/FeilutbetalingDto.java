package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties (ignoreUnknown = true)
public class FeilutbetalingDto {

    protected FeilutbetalingFakta behandlingFakta;

    public List<FeilutbetalingPerioder> getPerioder() {
        return this.behandlingFakta.getPerioder();
    }
}
