package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode = "6007")
public class OverstyrBeregning extends OverstyringsBekreftelse {

    protected long beregnetTilkjentYtelse;

    public OverstyrBeregning(long beregnetTilkjentYtelse) {
        super();
        this.beregnetTilkjentYtelse = beregnetTilkjentYtelse;
    }
}
