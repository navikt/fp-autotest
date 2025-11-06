package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

public class VurderSoknadsfristBekreftelse extends AksjonspunktBekreftelse {

    protected boolean erVilkarOk;

    public VurderSoknadsfristBekreftelse() {
        super();
    }

    public VurderSoknadsfristBekreftelse bekreftVilkårErOk() {
        erVilkarOk = true;
        return this;
    }

    public VurderSoknadsfristBekreftelse bekreftVilkårErIkkeOk() {
        erVilkarOk = false;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5007";
    }

}
