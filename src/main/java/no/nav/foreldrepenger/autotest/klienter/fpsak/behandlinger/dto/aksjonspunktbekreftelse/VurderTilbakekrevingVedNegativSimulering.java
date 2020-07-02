package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

@BekreftelseKode(kode="5084")
public class VurderTilbakekrevingVedNegativSimulering extends AksjonspunktBekreftelse {

    private VidereBehandling videreBehandling;
    private String varseltekst;

    public VurderTilbakekrevingVedNegativSimulering(){
        super();
    }

    private void setVidereBehandling(VidereBehandling videreBehandling) {
        this.videreBehandling = videreBehandling;
    }
    private void setVarseltekst(String varseltekst) {
        this.varseltekst = varseltekst;
    }

    public VurderTilbakekrevingVedNegativSimulering setTilbakekrevingMedVarsel(){
        setTilbakekrevingMedVarsel("Dette er friteksten i varselbrevet. Her skriver saksbehandler litt om hvorfor det er oppstått en feilutbetaling");
        return this;
    }
    public VurderTilbakekrevingVedNegativSimulering setTilbakekrevingMedVarsel(String varseltekst){
        setVidereBehandling(VidereBehandling.TILBAKEKR_INFOTRYGD);
        setVarseltekst(varseltekst);
        return this;
    }
    public VurderTilbakekrevingVedNegativSimulering setTilbakekrevingIgnorer(){
        setVidereBehandling(VidereBehandling.TILBAKEKR_IGNORER);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderTilbakekrevingVedNegativSimulering that = (VurderTilbakekrevingVedNegativSimulering) o;
        return videreBehandling == that.videreBehandling &&
                Objects.equals(varseltekst, that.varseltekst);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videreBehandling, varseltekst);
    }

    @Override
    public String toString() {
        return "VurderTilbakekrevingVedNegativSimulering{" +
                "videreBehandling=" + videreBehandling +
                ", varseltekst='" + varseltekst + '\'' +
                "} " + super.toString();
    }
}
