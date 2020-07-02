package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeilutbetalingDto {

    protected FeilutbetalingFakta behandlingFakta;

    public List<FeilutbetalingPerioder> getPerioder() {
        return this.behandlingFakta.getPerioder();
    }
}
