package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Stonadskontoer {
    protected Stønadskonto stonadskontotype;
    protected int maxDager;
    protected  int saldo;
    //protected  List<AktivitetSaldoDto> aktivitetSaldoDtoList;

    public Stønadskonto getStonadskontotype() {
        return this.stonadskontotype;
    }

    public int getMaxDager() {return this.maxDager;}

    public int getSaldo() {return this.saldo;}

}
