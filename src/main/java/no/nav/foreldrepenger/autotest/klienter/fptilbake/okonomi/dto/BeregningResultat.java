package no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeregningResultat {
    protected List<BeregningResultatPerioder> beregningResultatPerioder;

    public List<BeregningResultatPerioder> getBeregningResultatPerioderList() {
        return beregningResultatPerioder;
    }
}
