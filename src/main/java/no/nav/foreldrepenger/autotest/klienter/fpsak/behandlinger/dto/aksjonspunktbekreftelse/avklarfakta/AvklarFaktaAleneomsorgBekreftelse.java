package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode="5060")
public class AvklarFaktaAleneomsorgBekreftelse extends AksjonspunktBekreftelse{

    protected Boolean aleneomsorg;

    public AvklarFaktaAleneomsorgBekreftelse(Fagsak fagsak, Behandling behandling) {
        super(fagsak, behandling);
    }

    public void  bekreftBrukerHarAleneomsorg() {
        this.aleneomsorg = true;
    }

    public void  bekreftBrukerHarIkkeAleneomsorg() {
        this.aleneomsorg = false;
    }

}
