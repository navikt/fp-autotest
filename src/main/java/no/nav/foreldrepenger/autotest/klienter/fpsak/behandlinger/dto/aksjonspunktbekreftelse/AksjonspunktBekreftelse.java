package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

public abstract class AksjonspunktBekreftelse {

    protected String kode;
    protected String begrunnelse;

    @JsonProperty("@type")
    public abstract String aksjonspunktKode();

    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {

    }

    public AksjonspunktBekreftelse setBegrunnelse(String begrunnelse) {
        this.begrunnelse = begrunnelse;
        return this;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": {kode:" + (kode != null ? kode : "") + ", begrunnelse:" +
                (begrunnelse != null ? begrunnelse : "") + "}";
    }
}
