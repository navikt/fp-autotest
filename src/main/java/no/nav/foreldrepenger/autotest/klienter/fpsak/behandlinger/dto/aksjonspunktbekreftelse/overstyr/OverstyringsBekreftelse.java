package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

public abstract class OverstyringsBekreftelse extends AksjonspunktBekreftelse {

    protected String avslagskode;
    protected boolean erVilkarOk = true;
    
    public OverstyringsBekreftelse() {
        super();
    }

    public void overstyr(boolean erVilkarOk, String årsak) {
        this.erVilkarOk = erVilkarOk;
        this.avslagskode = årsak;
    }

    public void godkjenn() {
        overstyr(true, null);
    }

    public void avvis(Kode årsak) {
        overstyr(false, årsak.kode);
    }
}
