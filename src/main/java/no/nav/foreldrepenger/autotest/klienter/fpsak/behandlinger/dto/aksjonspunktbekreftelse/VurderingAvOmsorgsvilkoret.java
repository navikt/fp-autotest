package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;



@BekreftelseKode(kode="5011")
public class VurderingAvOmsorgsvilkoret extends AksjonspunktBekreftelse {

    protected String avslagskode;
    protected boolean erVilkarOk;

    public VurderingAvOmsorgsvilkoret() {
        super();
    }

    public VurderingAvOmsorgsvilkoret bekreftGodkjent() {
        erVilkarOk = true;
        return this;
    }

    public VurderingAvOmsorgsvilkoret bekreftAvvist(Kode kode) {
        erVilkarOk = false;
        avslagskode = kode.kode;
        return this;
    }

}
