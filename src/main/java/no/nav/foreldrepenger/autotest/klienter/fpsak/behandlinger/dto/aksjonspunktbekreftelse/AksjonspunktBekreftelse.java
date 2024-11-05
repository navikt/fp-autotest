package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

public abstract class AksjonspunktBekreftelse {

    @JsonProperty("@type")
    protected String kode;
    protected String begrunnelse;

    @SuppressWarnings("unused")
    protected AksjonspunktBekreftelse() {
        if (null == this.getClass().getAnnotation(BekreftelseKode.class)) {
            throw new RuntimeException("Kode annotation er ikke satt for " + this.getClass().getTypeName());
        }
        kode = this.getClass().getAnnotation(BekreftelseKode.class).kode();
    }

    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {

    }

    public String kode() {
        return kode;
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
