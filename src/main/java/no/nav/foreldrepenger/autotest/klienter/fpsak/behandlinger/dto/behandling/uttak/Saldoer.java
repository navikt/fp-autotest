package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Saldoer(Map<SaldoVisningStønadskontoType, Stonadskontoer> stonadskontoer) {

    public enum SaldoVisningStønadskontoType {
        MØDREKVOTE,
        FEDREKVOTE,
        FELLESPERIODE,
        FORELDREPENGER,
        FORELDREPENGER_FØR_FØDSEL,
        FLERBARNSDAGER,
        UTEN_AKTIVITETSKRAV;
    }
}
