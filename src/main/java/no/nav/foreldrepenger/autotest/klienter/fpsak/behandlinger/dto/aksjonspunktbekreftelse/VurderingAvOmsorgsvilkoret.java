package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;

@BekreftelseKode(kode = "5011")
public class VurderingAvOmsorgsvilkoret extends AksjonspunktBekreftelse {

    protected Avslagsårsak avslagskode;
    protected boolean erVilkarOk;

    public VurderingAvOmsorgsvilkoret() {
        super();
    }

    public VurderingAvOmsorgsvilkoret bekreftGodkjent() {
        erVilkarOk = true;
        return this;
    }

    public VurderingAvOmsorgsvilkoret bekreftAvvist(Avslagsårsak kode) {
        erVilkarOk = false;
        avslagskode = kode;
        return this;
    }

}
