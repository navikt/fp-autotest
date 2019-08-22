package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;


@BekreftelseKode(kode = "5092")
public class BekreftSvangerskapspengervilkår extends AksjonspunktBekreftelse {

    protected String begrunnelse;
    protected Boolean erVilkarOk;

    public BekreftSvangerskapspengervilkår(Fagsak fagsak, Behandling behandling) {
        super(fagsak, behandling);
    }

    public BekreftSvangerskapspengervilkår godkjenn(){
        this.erVilkarOk = true;
        return this;
    }


}
