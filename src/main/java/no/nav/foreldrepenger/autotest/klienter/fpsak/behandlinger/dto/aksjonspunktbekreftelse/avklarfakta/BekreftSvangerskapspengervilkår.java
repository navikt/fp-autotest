package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode = "5092")
public class BekreftSvangerskapspengervilkår extends AksjonspunktBekreftelse {

    protected String begrunnelse;
    protected Boolean erVilkarOk;

    public BekreftSvangerskapspengervilkår() {
        super();
    }

    public BekreftSvangerskapspengervilkår godkjenn() {
        this.erVilkarOk = true;
        return this;
    }

}
