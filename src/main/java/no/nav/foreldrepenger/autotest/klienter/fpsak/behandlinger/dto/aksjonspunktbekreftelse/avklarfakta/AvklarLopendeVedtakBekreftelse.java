package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Kode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;

@BekreftelseKode(kode = "5031")
public class AvklarLopendeVedtakBekreftelse extends AksjonspunktBekreftelse {

    protected boolean erVilkarOk;
    protected String avslagskode;

    public AvklarLopendeVedtakBekreftelse() {
        super();
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
