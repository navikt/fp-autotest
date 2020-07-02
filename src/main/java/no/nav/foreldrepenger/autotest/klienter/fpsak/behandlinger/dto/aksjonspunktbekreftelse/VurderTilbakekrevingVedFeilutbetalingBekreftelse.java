package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5090")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderTilbakekrevingVedFeilutbetalingBekreftelse extends AksjonspunktBekreftelse {
    private boolean hindreTilbaketrekk;

    public VurderTilbakekrevingVedFeilutbetalingBekreftelse() {
        super();
    }

    public boolean isHindreTilbaketrekk() {
        return hindreTilbaketrekk;
    }

    public void setTilbakekrevFrasøker(boolean tilbakekrevFrasøker) {
        this.hindreTilbaketrekk = !tilbakekrevFrasøker;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderTilbakekrevingVedFeilutbetalingBekreftelse that = (VurderTilbakekrevingVedFeilutbetalingBekreftelse) o;
        return hindreTilbaketrekk == that.hindreTilbaketrekk;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hindreTilbaketrekk);
    }

    @Override
    public String toString() {
        return "VurderTilbakekrevingVedFeilutbetalingBekreftelse{" +
                "hindreTilbaketrekk=" + hindreTilbaketrekk +
                "} " + super.toString();
    }
}
