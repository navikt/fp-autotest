package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import com.fasterxml.jackson.annotation.JsonProperty;

@BekreftelseKode(kode="5084")
public class VurderTilbakekrevingVedNegativSimulering extends AksjonspunktBekreftelse {

    protected VidereBehandling videreBehandling;
    protected String varseltekst;
    @JsonProperty("@type")
    protected String kode;

    public VurderTilbakekrevingVedNegativSimulering(){
        this.setBegrunnelse("Dette er begrunnelsen");
        this.kode = "5084";
    }

    private void setVidereBehandling(VidereBehandling videreBehandling) {
        this.videreBehandling = videreBehandling;
    }
    private void setVarseltekst(String varseltekst) {
        this.varseltekst = varseltekst;
    }

    public void setTilbakekrevingMedVarsel(){
        setTilbakekrevingMedVarsel("Dette er friteksten i varselbrevet. Her skriver saksbehandler litt om hvorfor det er oppstått en feilutbetaling");
    }
    public void setTilbakekrevingMedVarsel(String varseltekst){
        setVidereBehandling(VidereBehandling.TILBAKEKR_INFOTRYGD);
        setVarseltekst(varseltekst);
    }
    public void setTilbakekrevingIgnorer(){
        setVidereBehandling(VidereBehandling.TILBAKEKR_IGNORER);
    }
}
