package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;


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

    public OverstyringsBekreftelse godkjenn() {
        overstyr(true, null);
        return this;
    }

    public OverstyringsBekreftelse avvis(Avslagsårsak årsak) {
        overstyr(false, årsak.getKode());
        return this;
    }
}
