package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;

public class VurderingAvOmsorgsvilkoret extends AksjonspunktBekreftelse {

    protected String avslagskode;
    protected boolean erVilkarOk;

    public VurderingAvOmsorgsvilkoret bekreftGodkjent() {
        erVilkarOk = true;
        return this;
    }

    public VurderingAvOmsorgsvilkoret bekreftAvvist(Avslagsårsak kode) {
        erVilkarOk = false;
        avslagskode = kode.getKode();
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5011";
    }
}
