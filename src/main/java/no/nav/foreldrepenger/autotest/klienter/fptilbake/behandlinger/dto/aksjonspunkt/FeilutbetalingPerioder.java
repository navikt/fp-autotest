package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeilutbetalingPerioder {

    public LocalDate fom;
    public LocalDate tom;
    public String belop;

}
