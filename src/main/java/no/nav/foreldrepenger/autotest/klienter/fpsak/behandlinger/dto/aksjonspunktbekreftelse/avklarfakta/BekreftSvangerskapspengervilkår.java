package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class BekreftSvangerskapspengervilkår extends AksjonspunktBekreftelse {

    protected String begrunnelse;
    protected Boolean erVilkårOk;

    public BekreftSvangerskapspengervilkår godkjenn() {
        this.erVilkårOk = true;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5092";
    }
}
