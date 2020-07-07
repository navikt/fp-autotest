package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode = "5031")
public class AvklarLopendeVedtakBekreftelse extends AksjonspunktBekreftelse {

    protected boolean erVilkarOk;
    protected String avslagskode;

    public AvklarLopendeVedtakBekreftelse() {
        super();
        // TODO Auto-generated constructor stub
    }

    public AvklarLopendeVedtakBekreftelse bekreftGodkjent() {
        erVilkarOk = true;
        return this;
    }

    public AvklarLopendeVedtakBekreftelse bekreftAvvist(Kode avslagskode) {
        erVilkarOk = false;
        this.avslagskode = avslagskode.kode;
        return this;
    }

}
