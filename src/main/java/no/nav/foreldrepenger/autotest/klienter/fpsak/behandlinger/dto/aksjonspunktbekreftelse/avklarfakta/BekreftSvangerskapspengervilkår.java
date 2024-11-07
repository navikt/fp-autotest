package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class BekreftSvangerskapspengervilkår extends AksjonspunktBekreftelse {

    protected String begrunnelse;
    protected Boolean erVilkarOk;

    public BekreftSvangerskapspengervilkår godkjenn() {
        this.erVilkarOk = true;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5092";
    }
}
