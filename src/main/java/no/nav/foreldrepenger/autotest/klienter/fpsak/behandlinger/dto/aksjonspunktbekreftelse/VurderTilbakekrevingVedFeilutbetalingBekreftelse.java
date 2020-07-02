package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode = "5090")
public class VurderTilbakekrevingVedFeilutbetalingBekreftelse extends AksjonspunktBekreftelse {
    protected boolean hindreTilbaketrekk;

    public void setTilbakekrevFrasøker(boolean tilbakekrevFrasøker) {
        this.hindreTilbaketrekk = !tilbakekrevFrasøker;
    }
}
