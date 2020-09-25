package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeregningResultat {
    protected List<BeregningResultatPerioder> beregningResultatPerioder;

    public List<BeregningResultatPerioder> getBeregningResultatPerioderList() {
        return beregningResultatPerioder;
    }
}
