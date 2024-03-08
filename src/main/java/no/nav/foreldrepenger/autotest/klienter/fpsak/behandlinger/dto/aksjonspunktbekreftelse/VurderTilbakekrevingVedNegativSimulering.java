package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode="5084")
public class VurderTilbakekrevingVedNegativSimulering extends AksjonspunktBekreftelse {

    protected VidereBehandling videreBehandling;
    protected String varseltekst;

    public VurderTilbakekrevingVedNegativSimulering(){
        super();
        this.setBegrunnelse("Dette er begrunnelsen");
    }

    public VurderTilbakekrevingVedNegativSimulering tilbakekrevingMedVarsel(){
        tilbakekrevingMedVarsel("Dette er friteksten i varselbrevet. Her skriver saksbehandler litt om hvorfor det er oppstått en feilutbetaling");
        return this;
    }

    public VurderTilbakekrevingVedNegativSimulering tilbakekrevingUtenVarsel(){
        setVidereBehandling(VidereBehandling.TILBAKEKR_OPPRETT);
        return this;
    }

    public VurderTilbakekrevingVedNegativSimulering avventSamordningIngenTilbakekreving(){
        setVidereBehandling(VidereBehandling.TILBAKEKR_IGNORER);
        return this;
    }

    private void setVidereBehandling(VidereBehandling videreBehandling) {
        this.videreBehandling = videreBehandling;
    }
    private void setVarseltekst(String varseltekst) {
        this.varseltekst = varseltekst;
    }

    private void tilbakekrevingMedVarsel(String varseltekst){
        setVidereBehandling(VidereBehandling.TILBAKEKR_OPPRETT);
        setVarseltekst(varseltekst);
    }

}
