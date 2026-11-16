package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class BekreftSvangerskapspengervilkår extends AksjonspunktBekreftelse {

    protected String avslagskode;

    public BekreftSvangerskapspengervilkår godkjenn() {
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5092";
    }
}
