package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Stønadskonti(Saldoer.SaldoVisningStønadskontoType stonadskontoType, int maxDager, int saldo) {

}
